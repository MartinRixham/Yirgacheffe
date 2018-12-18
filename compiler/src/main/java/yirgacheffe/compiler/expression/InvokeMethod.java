package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Functions;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.function.Methods;
import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.lang.reflect.Method;

public class InvokeMethod implements Expression
{
	private Coordinate coordinate;

	private String name;

	private String caller;

	private Expression owner;

	private Array<Expression> arguments;

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

	public Type getType(Variables variables)
	{
		Type ownerType = this.owner.getType(variables);
		Class<?> ownerClass = ownerType.reflectionClass();
		Method[] methods = ownerClass.getDeclaredMethods();
		Type returnType = new NullType();

		for (Method method: methods)
		{
			if (method.getName().equals(this.name))
			{
				returnType = new Function(ownerType, method).getReturnType();
			}
		}

		if (returnType == PrimitiveType.INT ||
			returnType == PrimitiveType.LONG ||
			returnType == PrimitiveType.FLOAT)
		{
			return PrimitiveType.DOUBLE;
		}

		return returnType;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Type> argumentTypes = new Array<>();

		for (Expression argument: this.arguments)
		{
			argumentTypes.push(argument.getType(variables));
		}

		Arguments arguments = new Arguments(argumentTypes);
		Type owner = this.owner.getType(variables);
		Methods methods = new Methods(owner, this.caller);
		Array<Callable> namedMethods = methods.getMethodsNamed(this.name);
		String method = owner + "." + this.name;
		Functions functions = new Functions(this.coordinate, method, namedMethods, false);
		MatchResult matchResult = functions.getMatchingExecutable(arguments);
		Callable function = matchResult.getFunction();
		Array<Type> parameters = function.getParameterTypes();
		Array<Error> errors = this.owner.compile(methodVisitor, variables);

		if (owner instanceof PrimitiveType)
		{
			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				this.withSlashes(owner),
				"valueOf",
				"(" + owner.toJVMType() + ")L" + this.withSlashes(owner) + ";",
				false);
		}

		for (int i = 0; i < Math.min(this.arguments.length(), parameters.length()); i++)
		{
			Expression argument = this.arguments.get(i);
			Type argumentType = argumentTypes.get(i);

			errors = errors.concat(argument.compile(methodVisitor, variables));

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

		int invocationOpcode =
			isInterface ? Opcodes.INVOKEINTERFACE :
				function.isPublic() ? Opcodes.INVOKEVIRTUAL :
					Opcodes.INVOKESPECIAL;

		methodVisitor.visitMethodInsn(
			invocationOpcode,
			this.withSlashes(owner),
			function.getName(),
			function.getDescriptor(),
			isInterface);

		Type returnType = matchResult.getFunction().getReturnType();

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

		return matchResult.getErrors().concat(errors);
	}

	public Expression getFirstOperand()
	{
		return this.owner.getFirstOperand();
	}

	private String withSlashes(Type type)
	{
		return type.toFullyQualifiedType().replace(".", "/");
	}

	public Array<VariableRead> getVariableReads()
	{
		Array<VariableRead> variableReads = new Array<>();

		for (Expression argument: this.arguments)
		{
			variableReads = variableReads.concat(argument.getVariableReads());
		}

		variableReads = variableReads.concat(this.owner.getVariableReads());

		return variableReads;
	}
}
