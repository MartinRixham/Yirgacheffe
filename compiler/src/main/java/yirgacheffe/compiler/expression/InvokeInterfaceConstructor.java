package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.AmbiguousMatchResult;
import yirgacheffe.compiler.function.Arguments;
import yirgacheffe.compiler.function.FailedMatchResult;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.lang.reflect.Method;

public class InvokeInterfaceConstructor implements Expression
{
	private Coordinate coordinate;

	private Type owner;

	private Array<Expression> arguments;

	public InvokeInterfaceConstructor(
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
		return this.owner;
	}

	public Result compile(Variables variables)
	{
		if (this.owner instanceof NullType)
		{
			variables.stackPush(this.owner);

			return new Result();
		}

		Arguments arguments = new Arguments(this.arguments, variables);
		MatchResult matchResult = new FailedMatchResult();

		for (Function method: this.getMethods())
		{
			matchResult = matchResult.betterOf(arguments.matches(method));
		}

		Array<Type> parameterTypes = matchResult.getParameterTypes();

		String descriptor =
			"(" + arguments.getDescriptor(parameterTypes) + ")" +
				this.owner.toJVMType();

		Result result = new Result()
			.concat(matchResult.compileArguments(variables))
			.concat(this.coordinate.compile())
			.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				this.owner.toFullyQualifiedType(),
				"0this",
				descriptor,
				false))
			.concat(this.getError(matchResult, arguments));

		variables.stackPush(this.owner);

		return result;
	}

	private Array<Function> getMethods()
	{
		Array<Function> constructorMethods = new Array<>();
		Method[] methods = this.owner.reflectionClass().getMethods();

		for (Method method: methods)
		{
			if (method.getName().equals("0this"))
			{
				constructorMethods.push(new Function(owner, method));
			}
		}

		return constructorMethods;
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
}
