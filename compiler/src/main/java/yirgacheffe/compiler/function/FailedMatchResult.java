package yirgacheffe.compiler.function;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class FailedMatchResult implements MatchResult
{
	public MatchResult betterOf(MatchResult other)
	{
		return other;
	}

	public int score()
	{
		return -1;
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
