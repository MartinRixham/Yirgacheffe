package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Functions;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.statement.StatementResult;
import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Method;

public class InvokeMethod implements Expression
{
	private Coordinate coordinate;

	private String name;

	private String caller;

	private Expression owner;

	private Array<Expression> arguments;

	private Type ownerType;

	private Array<Type> argumentTypes = new Array<>();

	private MatchResult matchResult;

	private Type returnType;

	public InvokeMethod(
		Coordinate coordinate,
		String name,
		String caller,
		Expression owner,
		Array<Expression> arguments)
	{
		this.coordinate = coordinate;
		this.name = name;
		this.caller = caller;
		this.owner = owner;
		this.arguments = arguments;
	}

	@Override
	public Type check(StatementResult result)
	{
		this.ownerType = this.owner.check(result);

		for (Expression argument: this.arguments)
		{
			this.argumentTypes.push(argument.check(result));
		}

		Arguments arguments = new Arguments(this.argumentTypes);

		this.matchResult =
			this.getMatchResult(
				this.ownerType,
				this.name,
				arguments);

		Callable function = this.matchResult.getFunction();
		this.returnType = function.getReturnType();

		for (Error error: this.matchResult.getErrors())
		{
			result.error(error);
		}

		return function.getReturnType();
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		Callable function = this.matchResult.getFunction();
		Array<Type> parameters = function.getParameterTypes();
		Type owner = this.ownerType;
		Type returnType = this.returnType;

		this.owner.compile(methodVisitor);

		if (owner instanceof PrimitiveType)
		{
			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				this.withSlashes(owner),
				"valueOf",
				"(" + owner.toJVMType() + ")L" + this.withSlashes(owner) + ";",
				false);
		}

		for (int i = 0; i < this.arguments.length(); i++)
		{
			Expression argument = this.arguments.get(i);
			Type argumentType = this.argumentTypes.get(i);

			argument.compile(methodVisitor);

			if (argumentType instanceof PrimitiveType &&
				parameters.get(i) instanceof ReferenceType)
			{

				String descriptor =
					"(" + argumentType.toJVMType() + ")L" +
					this.withSlashes(argumentType) + ";";

				methodVisitor.visitMethodInsn(
					Opcodes.INVOKESTATIC,
					this.withSlashes(argumentType),
					"valueOf",
					descriptor,
					false);
			}
		}

		boolean isInterface = owner.reflectionClass().isInterface();

		methodVisitor.visitMethodInsn(
			isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL,
			this.withSlashes(owner),
			function.getName(),
			function.getDescriptor(),
			isInterface);

		if (returnType instanceof GenericType)
		{
			methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, this.withSlashes(returnType));

			Type type = ((GenericType) returnType).unwrap();

			if (type instanceof PrimitiveType)
			{
				methodVisitor.visitMethodInsn(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Boxer",
					"ofValue",
					"(L" + this.withSlashes(type) + ";)" + type.toJVMType(),
					false);
			}
		}
		else if (returnType.equals(PrimitiveType.INT))
		{
			methodVisitor.visitInsn(Opcodes.I2D);
		}
		else if (returnType.equals(PrimitiveType.LONG))
		{
			methodVisitor.visitInsn(Opcodes.L2D);
		}
		else if (returnType.equals(PrimitiveType.FLOAT))
		{
			methodVisitor.visitInsn(Opcodes.F2D);
		}
	}

	private MatchResult getMatchResult(
		Type owner,
		String methodName,
		Arguments arguments)
	{
		Method[] methods;

		if (owner.toFullyQualifiedType().equals(this.caller))
		{
			methods = owner.reflectionClass().getDeclaredMethods();
		}
		else
		{
			methods = owner.reflectionClass().getMethods();
		}

		Array<Callable> namedMethods = new Array<>();

		for (Method method: methods)
		{
			if (method.getName().equals(methodName))
			{
				namedMethods.push(new Function(owner, method));
			}
		}

		String method = owner + "." + methodName;

		return new Functions(this.coordinate, method, namedMethods, false)
			.getMatchingExecutable(arguments);
	}

	private String withSlashes(Type type)
	{
		return type.toFullyQualifiedType().replace(".", "/");
	}
}
