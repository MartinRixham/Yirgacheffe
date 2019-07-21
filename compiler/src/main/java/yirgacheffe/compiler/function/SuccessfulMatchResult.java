package yirgacheffe.compiler.function;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class SuccessfulMatchResult implements MatchResult
{
	private Function function;

	private Arguments arguments;

	private Array<MismatchedTypes> mismatchedParameters;

	private int score;

	public SuccessfulMatchResult(
		Function function,
		Arguments arguments,
		int score,
		Array<MismatchedTypes> mismatchedParameters)
	{
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
			variables);
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

	public Array<MismatchedTypes> getMismatchedParameters()
	{
		return this.mismatchedParameters;
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
			return new AmbiguousMatchResult(this.score);
		}
	}

	public int score()
	{
		return this.score;
	}
}
