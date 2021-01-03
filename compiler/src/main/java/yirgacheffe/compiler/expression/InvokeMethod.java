package yirgacheffe.compiler.expression;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.function.Caller;
import yirgacheffe.compiler.function.FailedMatchResult;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.LengthMethods;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.statement.TailCall;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.ArrayType;
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
import java.util.Set;

public class InvokeMethod implements Expression, Parameterisable
{
	private Coordinate coordinate;

	private String name;

	private Caller caller;

	private Expression owner;

	private Array<Expression> arguments;

	private LengthMethods lengthMethods = new LengthMethods();

	public InvokeMethod(
		Coordinate coordinate,
		String name,
		Caller caller,
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
		Interface members = ownerType.reflect();
		Set<Function> methods = members.getMethods();
		Type returnType = new NullType();

		for (Function method: methods)
		{
			if (method.getName().equals(this.name))
			{
				if (this.lengthMethods.contains(method))
				{
					return PrimitiveType.INT;
				}

				returnType = this.caller.lookup(method.getReturnType());
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
		Type owner = this.owner.getType(variables);
		Array<Function> namedMethods = this.getMethodsNamed(owner, this.name);
		String name = "method " + owner + "." + this.name;

		Arguments arguments =
			new Arguments(
				this.coordinate,
				name,
				this.arguments,
				variables);

		MatchResult matchResult = arguments.matches();

		for (Function function: namedMethods)
		{
			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		MethodType methodType =
			MethodType.methodType(
				CallSite.class,
				MethodHandles.Lookup.class,
				String.class,
				MethodType.class);

		boolean isPrivate = this.caller.equals(owner);

		Handle bootstrapMethod =
			new Handle(
				Opcodes.H_INVOKESTATIC,
				Bootstrap.class.getName().replace(".", "/"),
				isPrivate ? "bootstrapPrivate" : "bootstrapPublic",
				methodType.toMethodDescriptorString(),
				false);

		String ownerDescriptor = owner.toFullyQualifiedType();
		Array<Type> parameterTypes = matchResult.getParameterTypes();

		String descriptor =
			'(' + (ownerDescriptor.charAt(0) != '[' ? 'L' +
			ownerDescriptor + ';' : ownerDescriptor) +
			arguments.getDescriptor(parameterTypes) + ')' +
			matchResult.getReturnType().toJVMType();

		Type returnType = matchResult.getReturnType();

		Result result = new Result()
			.concat(this.owner.compile(variables))
			.concat(owner.convertTo(new ReferenceType(Object.class)))
			.concat(matchResult.compileArguments(variables))
			.concat(this.coordinate.compile())
			.add(new InvokeDynamicInsnNode(
				matchResult.getName(),
				descriptor,
				bootstrapMethod))
			.concat(this.cacheSignature(returnType))
			.concat(returnType.convertTo(this.getType(variables)));

		variables.stackPop();
		variables.stackPush(returnType);

		return result;
	}

	private Result cacheSignature(Type returnType)
	{
		if (returnType.hasParameter())
		{
			return new Result()
				.add(new InsnNode(Opcodes.DUP))
				.add(new LdcInsnNode(String.join(",", returnType.getSignatureTypes())))
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Bootstrap",
					"cacheObjectSignature",
					"(Ljava/lang/Object;Ljava/lang/String;)V",
					false));
		}
		else
		{
			return new Result();
		}
	}

	public Result compileArguments(Variables variables)
	{
		Type owner = this.owner.getType(variables);
		Array<Function> namedMethods = this.getMethodsNamed(owner, this.name);
		String name = "method " + owner + "." + this.name;
		MatchResult matchResult = new FailedMatchResult(this.coordinate, name);

		Arguments arguments =
			new Arguments(
				this.coordinate,
				name,
				this.arguments,
				variables);

		for (Function function: namedMethods)
		{
			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		return matchResult.compileArguments(variables);
	}

	public Array<Function> getMethodsNamed(Type owner, String name)
	{
		Interface methods = owner.reflect();
		Set<Function> methodSet = methods.getPublicMethods();

		if (this.caller.equals(owner))
		{
			methodSet.addAll(methods.getMethods());
		}

		Array<Function> namedMethods = new Array<>();

		for (Function method: methodSet)
		{
			if (method.isNamed(name))
			{
				namedMethods.push(method);
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

	public Coordinate getCoordinate()
	{
		return this.coordinate;
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
