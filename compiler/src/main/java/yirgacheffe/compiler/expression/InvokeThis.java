package yirgacheffe.compiler.expression;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.AmbiguousMatchResult;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.function.FailedMatchResult;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Bootstrap;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

public class InvokeThis implements Expression
{
	private Coordinate coordinate;

	private Type owner;

	private Array<Expression> arguments;

	public InvokeThis(
		Coordinate coordinate,
		Type owner,
		Array<Expression> arguments)
	{
		this.coordinate = coordinate;
		this.owner = owner;
		this.arguments = arguments;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.VOID;
	}

	public Result compile(Variables variables)
	{
		Constructor<?>[] constructors = this.owner.reflectionClass().getConstructors();
		Arguments arguments = new Arguments(this.arguments, variables);
		MatchResult matchResult = new FailedMatchResult();

		for (Constructor<?> constructor : constructors)
		{
			Function function = new Function(this.owner, constructor);

			matchResult = matchResult.betterOf(arguments.matches(function));
		}

		Array<Type> parameterTypes = matchResult.getParameterTypes();
		StringBuilder descriptor = new StringBuilder("(");

		descriptor.append(this.owner.toJVMType());
		descriptor.append(arguments.getDescriptor(parameterTypes));
		descriptor.append(")V");

		MethodType methodType =
			MethodType.methodType(
				CallSite.class,
				MethodHandles.Lookup.class,
				String.class,
				MethodType.class);

		Handle bootstrapMethod =
			new Handle(
				Opcodes.H_INVOKESTATIC,
				Bootstrap.class.getName().replace(".", "/"),
				"bootstrapPrivate",
				methodType.toMethodDescriptorString(),
				false);

		Result result = new Result()
			.concat(this.getError(matchResult, arguments))
			.add(new VarInsnNode(Opcodes.ALOAD, 0))
			.concat(matchResult.compileArguments(variables))
			.concat(this.coordinate.compile())
			.add(new InvokeDynamicInsnNode(
				"0this",
				descriptor.toString(),
				bootstrapMethod));

		variables.stackPush(this.owner);

		return result;
	}

	private Result getError(MatchResult matchResult, Arguments arguments)
	{
		Result result = new Result();

		if (matchResult instanceof FailedMatchResult)
		{
			String message =
				"Constructor " + this.owner + arguments + " not found.";

			result = result.add(new Error(this.coordinate, message));
		}
		else if (matchResult instanceof AmbiguousMatchResult)
		{
			String message =
				"Ambiguous call to constructor " + this.owner + arguments + ".";

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

		return variableReads;
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}
}
