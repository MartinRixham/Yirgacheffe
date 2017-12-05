package yirgacheffe;

public class CompilationResult
{
	private byte[] bytecode;

	private String[] errors;

	public CompilationResult(byte[] bytecode)
	{
		this.bytecode = bytecode;
	}

	public CompilationResult(String[] errors)
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
