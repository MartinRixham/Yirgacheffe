package yirgacheffe.compiler;

import java.lang.reflect.Executable;

public class MatchResult<T extends Executable>
{
	private T executable;

	private String descriptor;

	public MatchResult()
	{
	}

	public MatchResult(T executable, String descriptor)
	{
		this.executable = executable;
		this.descriptor = descriptor;
	}

	public boolean isSuccessful()
	{
		return this.executable != null;
	}

	public T getExecutable()
	{
		return this.executable;
	}

	public String getDescriptor()
	{
		return this.descriptor;
	}
}
