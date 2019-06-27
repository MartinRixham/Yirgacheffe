package yirgacheffe.compiler.expression;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Functions;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.function.Methods;
import yirgacheffe.compiler.statement.TailCall;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Bootstrap;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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
				break;
			}
		}

		if (returnType.equals(PrimitiveType.INT) ||
			returnType.equals(PrimitiveType.LONG) ||
			returnType.equals(PrimitiveType.FLOAT))
		{
			return PrimitiveType.DOUBLE;
		}

		if (returnType instanceof GenericType)
		{
			GenericType genericType = (GenericType) returnType;

			return genericType.unwrap();
		}

		return returnType;
	}

	public Result compile(Variables variables)
	{
		Arguments arguments = new Arguments(this.arguments, variables);
		Type owner = this.owner.getType(variables);
		Methods methods = new Methods(owner, this.caller);
		Array<Callable> namedMethods = methods.getMethodsNamed(this.name);
		String method = owner + "." + this.name;
		Functions functions = new Functions(this.coordinate, method, namedMethods, false);
		MatchResult matchResult = functions.getMatchingExecutable(arguments);
		Callable function = matchResult.getFunction();
		boolean variableArugments = function.hasVariableArguments();
		Array<Type> parameters = function.getParameterTypes();
		Result result = this.owner.compile(variables);

		if (owner.isPrimitive())
		{
			result = result.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				owner.toFullyQualifiedType(),
				"valueOf",
				"(" + owner.toJVMType() + ")L" + owner.toFullyQualifiedType() + ";",
				false));
		}

		result = result
			.concat(arguments.compile(parameters, variables, variableArugments))
			.concat(this.coordinate.compile());

		String ownerDescriptor = owner.toFullyQualifiedType();

		String descriptor =
			'(' + (ownerDescriptor.charAt(0) != '[' ? 'L' +
			ownerDescriptor + ';' : ownerDescriptor) +
			function.getDescriptor().substring(1);

		MethodType methodType =
			MethodType.methodType(
				CallSite.class,
				MethodHandles.Lookup.class,
				String.class,
				MethodType.class);

		boolean isPrivate = owner.toFullyQualifiedType().equals(this.caller);

		Handle bootstrapMethod =
			new Handle(
				Opcodes.H_INVOKESTATIC,
				Bootstrap.class.getName().replace(".", "/"),
				isPrivate ? "bootstrapPrivate" : "bootstrapPublic",
				methodType.toMethodDescriptorString(),
				false);

		result = result.add(
			new InvokeDynamicInsnNode(
				function.getName(),
				descriptor,
				bootstrapMethod));

		Type returnType = function.getReturnType();

		if (returnType instanceof GenericType)
		{
			result = result.add(
				new TypeInsnNode(
					Opcodes.CHECKCAST,
					returnType.toFullyQualifiedType()));

			if (returnType.isPrimitive())
			{
				result = result.add(
					new MethodInsnNode(
						Opcodes.INVOKESTATIC,
						"yirgacheffe/lang/Boxer",
						"ofValue",
						"(L" + returnType.toFullyQualifiedType() + ";)" +
							returnType.getSignature(),
						false));
			}
		}
		else if (returnType.equals(PrimitiveType.INT))
		{
			result = result.add(new InsnNode(Opcodes.I2D));
		}
		else if (returnType.equals(PrimitiveType.LONG))
		{
			result = result.add(new InsnNode(Opcodes.L2D));
		}
		else if (returnType.equals(PrimitiveType.FLOAT))
		{
			result = result.add(new InsnNode(Opcodes.F2D));
		}

		return result.concat(matchResult.getResult());
	}

	public Result compileArguments(Variables variables)
	{
		Arguments arguments = new Arguments(this.arguments, variables);
		Type owner = this.owner.getType(variables);
		Methods methods = new Methods(owner, this.caller);
		Array<Callable> namedMethods = methods.getMethodsNamed(this.name);
		String method = owner + "." + this.name;
		Functions functions = new Functions(this.coordinate, method, namedMethods, false);
		MatchResult matchResult = functions.getMatchingExecutable(arguments);
		Callable function = matchResult.getFunction();
		boolean variableArugments = function.hasVariableArguments();
		Array<Type> parameters = function.getParameterTypes();

		return arguments.compile(parameters, variables, variableArugments);
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
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

	public Array<Type> getParameters(Variables variables)
	{
		Array<Type> parameters = new Array<>();

		for (Expression expression: this.arguments)
		{
			parameters.push(expression.getType(variables));
		}

		return parameters;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof TailCall)
		{
			TailCall tailCall = (TailCall) other;

			return this.owner instanceof This &&
				tailCall.equals(this.name, this.arguments);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return this.name.hashCode() + this.arguments.hashCode();
	}
}
