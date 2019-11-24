package yirgacheffe.compiler.expression;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.AmbiguousMatchResult;
import yirgacheffe.compiler.function.FailedMatchResult;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.statement.TailCall;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.type.ArrayType;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Bootstrap;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InvokeMethod implements Expression, Parameterisable
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

		if (returnType instanceof ArrayType)
		{
			ArrayType arrayType = (ArrayType) returnType;
			ReferenceType type = new ReferenceType(Array.class);

			return new ParameterisedType(type, new Array<>(arrayType.getElementType()));
		}

		return returnType;
	}

	public Result compile(Variables variables)
	{
		Arguments arguments = new Arguments(this.arguments, variables);
		Type owner = this.owner.getType(variables);
		Array<Function> namedMethods = this.getMethodsNamed(owner, this.name);
		MatchResult matchResult = new FailedMatchResult();

		for (Function function: namedMethods)
		{
			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		String ownerDescriptor = owner.toFullyQualifiedType();
		Array<Type> parameterTypes = matchResult.getParameterTypes();

		StringBuilder descriptor =
			new StringBuilder(
				'(' + (ownerDescriptor.charAt(0) != '[' ? 'L' +
				ownerDescriptor + ';' : ownerDescriptor));

		descriptor.append(arguments.getDescriptor(parameterTypes));
		descriptor.append(')');
		descriptor.append(matchResult.getReturnType().toJVMType());

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

		Type returnType = matchResult.getReturnType();

		Result result = new Result()
			.concat(this.owner.compile(variables))
			.concat(owner.convertTo(new ReferenceType(Object.class)))
			.concat(matchResult.compileArguments(variables))
			.concat(this.coordinate.compile())
			.add(new InvokeDynamicInsnNode(
				matchResult.getName(),
				descriptor.toString(),
				bootstrapMethod))
			.concat(returnType.convertTo(this.getType(variables)))
			.concat(this.getError(matchResult, owner, arguments));

		variables.stackPop();
		variables.stackPush(returnType);

		return result;
	}

	public Result compileArguments(Variables variables)
	{
		Arguments arguments = new Arguments(this.arguments, variables);
		Type owner = this.owner.getType(variables);
		Array<Function> namedMethods = this.getMethodsNamed(owner, this.name);
		MatchResult matchResult = new FailedMatchResult();

		for (Function function: namedMethods)
		{
			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		return matchResult.compileArguments(variables)
			.concat(this.getError(matchResult, owner, arguments));
	}

	private Result getError(
		MatchResult matchResult, Type owner, Arguments arguments)
	{
		Result result = new Result();

		if (matchResult instanceof FailedMatchResult)
		{
			String message =
				"Method " + owner + "." + this.name + arguments + " not found.";

			result = result.add(new Error(this.coordinate, message));
		}
		else if (matchResult instanceof AmbiguousMatchResult)
		{
			String message =
				"Ambiguous call to method " + owner + "." + this.name + arguments + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		for (MismatchedTypes mismatchedTypes: matchResult.getMismatchedParameters())
		{
			String message =
				"Argument of type " +
				mismatchedTypes.from() +
				" cannot be assigned to generic parameter of type " +
				mismatchedTypes.to() + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		return result;
	}

	public Array<Function> getMethodsNamed(Type owner, String name)
	{
		Set<Method> methodSet =
			new HashSet<>(Arrays.asList(owner.reflectionClass().getMethods()));

		if (owner.toFullyQualifiedType().equals(this.caller))
		{
			methodSet.addAll(
				Arrays.asList(owner.reflectionClass().getDeclaredMethods()));
		}

		Array<Function> namedMethods = new Array<>();

		for (Method method: methodSet)
		{
			if (method.getName().equals(name))
			{
				namedMethods.push(new Function(owner, method));
			}
		}

		return namedMethods;
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
