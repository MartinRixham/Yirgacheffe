package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.MismatchedTypes;

import java.util.ArrayList;
import java.util.List;

public class MatchResult
{
	private Callable function;

	private List<MismatchedTypes> mismatchedParameters;

	public MatchResult()
	{
		this.function = new NullFunction();
		this.mismatchedParameters = new ArrayList<>();
	}

	public MatchResult(Callable functions, List<MismatchedTypes> mismatchedParameters)
	{
		this.function = functions;
		this.mismatchedParameters = mismatchedParameters;
	}

	public boolean isSuccessful()
	{
		return !(this.function instanceof NullFunction);
	}

	public Callable getExecutable()
	{
		return this.function;
	}

	public List<MismatchedTypes> getMismatchedParameters()
	{
		return this.mismatchedParameters;
	}
}
