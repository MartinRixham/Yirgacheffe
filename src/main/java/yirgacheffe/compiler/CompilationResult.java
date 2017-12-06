package yirgacheffe.compiler;

import java.util.List;

public class CompilationResult
{
	private byte[] bytecode;

	private List<Error> errors;

	public CompilationResult(byte[] bytecode)
	{
		this.bytecode = bytecode;
	}

	public CompilationResult(List<Error> errors)
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
		String errors = "";

		for (Error error: this.errors)
		{
			errors += error.toString() + "\n";
		}

		return errors;
	}
}
