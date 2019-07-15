package yirgacheffe.compiler.function;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class AmbiguousMatchResult implements MatchResult
{
	private int score;

	public AmbiguousMatchResult(int score)
	{
		this.score = score;
	}

	public MatchResult betterOf(MatchResult other)
	{
		if (this.score < other.score())
		{
			return other;
		}
		else
		{
			return this;
		}
	}

	public int score()
	{
		return this.score;
	}

	public Result compileArguments(Variables variables)
	{
		return new Result();
	}

	public String getDescriptor()
	{
		return "()V";
	}

	public String getName()
	{
		return "";
	}

	public Type getReturnType()
	{
		return new NullType();
	}

	public Array<MismatchedTypes> getMismatchedParameters()
	{
		return new Array<>();
	}
}