package yirgacheffe.compiler;

import java.util.List;

public class CompilationResult
{
	private String classFileName;

	private byte[] bytecode;

	private List<Error> errors;

	public CompilationResult(String classFileName, byte[] bytecode)
	{
		this.classFileName = classFileName;
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

	public String getClassFileName()
	{
		return this.classFileName;
	}

	public String getErrors()
	{
		StringBuilder errors = new StringBuilder();

		for (Error error: this.errors)
		{
			errors.append(error.toString()).append("\n");
		}

		return errors.toString();
	}
}
