package yirgacheffe.compiler;

import java.util.List;

public class CompilationResult
{
	private byte[] bytecode;

	private List<String> errors;

	public CompilationResult(byte[] bytecode)
	{
		this.bytecode = bytecode;
	}

	public CompilationResult(List<String> errors)
	{
		this.errors = errors;
	}

	public byte[] getBytecode()
	{
		return this.bytecode;
	}

	public boolean isSuccessful()
	{
		return this.errors == null;
	}

	public String getErrors()
	{
		return String.join(" ", this.errors);
	}
}
