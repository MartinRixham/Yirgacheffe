package yirgacheffe.compiler;

import yirgacheffe.compiler.type.Function;
import yirgacheffe.compiler.type.MismatchedTypes;

import java.util.List;

public class MatchResult
{
	private Function executable;

	private String descriptor;

	private List<MismatchedTypes> mismatchedParameters;

	public MatchResult()
	{
	}

	public MatchResult(
		Function executable,
		String descriptor,
		List<MismatchedTypes> mismatchedParameters)
	{
		this.executable = executable;
		this.descriptor = descriptor;
		this.mismatchedParameters = mismatchedParameters;
	}

	public boolean isSuccessful()
	{
		return this.executable != null;
	}

	public Function getExecutable()
	{
		return this.executable;
	}

	public String getDescriptor()
	{
		return this.descriptor;
	}

	public List<MismatchedTypes> getMismatchedParameters()
	{
		return this.mismatchedParameters;
	}
}
