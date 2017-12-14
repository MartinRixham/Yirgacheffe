package yirgacheffe.compiler;

import yirgacheffe.compiler.error.Error;

import java.util.Collections;
import java.util.List;

public class CompilationResult
{
	private String sourceFileName;

	private String classFileName;

	private byte[] bytecode;

	private List<Error> errors;

	public CompilationResult(String classFileName, byte[] bytecode)
	{
		this.classFileName = classFileName;
		this.bytecode = bytecode;
	}

	public CompilationResult(String sourceFileName, List<Error> errors)
	{
		this.sourceFileName = sourceFileName;
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

	public String getSourceFileName()
	{
		return this.sourceFileName;
	}

	public String getClassFileName()
	{
		return this.classFileName;
	}

	public String getErrors()
	{
		Collections.sort(this.errors);

		StringBuilder errors = new StringBuilder();

		for (Error error: this.errors)
		{
			errors.append(error.toString()).append("\n");
		}

		return errors.toString();
	}
}
