package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.lang.Array;

public class MatchResult
{
	private Callable function;

	private Array<MismatchedTypes> mismatchedParameters;

	private boolean ambiguous;

	public MatchResult(boolean ambiguous)
	{
		this.function = new NullFunction();
		this.mismatchedParameters = new Array<>();
		this.ambiguous = ambiguous;
	}

	public MatchResult(Callable functions, Array<MismatchedTypes> mismatchedParameters)
	{
		this.function = functions;
		this.mismatchedParameters = mismatchedParameters;
	}

	public boolean isSuccessful()
	{
		return !(this.function instanceof NullFunction);
	}

	public Callable getFunction()
	{
		return this.function;
	}

	public Array<MismatchedTypes> getMismatchedParameters()
	{
		return this.mismatchedParameters;
	}

	public boolean isAmbiguous()
	{
		return this.ambiguous;
	}
}
