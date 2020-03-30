package yirgacheffe.compiler.function;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class SuccessfulMatchResult implements MatchResult
{
	private Coordinate coordinate;

	private String name;

	private Function function;

	private Arguments arguments;

	private Array<MismatchedTypes> mismatchedParameters;

	private int score;

	public SuccessfulMatchResult(
		Coordinate coordinate,
		String name,
		Function function,
		Arguments arguments,
		int score,
		Array<MismatchedTypes> mismatchedParameters)
	{
		this.coordinate = coordinate;
		this.name = name;
		this.function = function;
		this.arguments = arguments;
		this.score = score;
		this.mismatchedParameters = mismatchedParameters;
	}

	public Result compileArguments(Variables variables)
	{
		return this.arguments.compile(
			this.function.getParameterTypes(),
			this.function.hasVariableArguments(),
			variables)
			.concat(this.getError());
	}

	private Result getError()
	{
		Result result = new Result();

		for (MismatchedTypes mismatchedTypes: this.mismatchedParameters)
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

	public String getName()
	{
		return this.function.getName();
	}

	public Array<Type> getParameterTypes()
	{
		return this.function.getParameterTypes();
	}

	public Type getReturnType()
	{
		return this.function.getReturnType();
	}

	public MatchResult betterOf(MatchResult other)
	{
		if (this.score > other.score())
		{
			return this;
		}
		else if (this.score < other.score())
		{
			return other;
		}
		else
		{
			return new AmbiguousMatchResult(this.coordinate, this.name, this.score);
		}
	}

	public int score()
	{
		return this.score;
	}
}
