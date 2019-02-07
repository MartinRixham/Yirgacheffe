package yirgacheffe.compiler;

public class GeneratedClass
{
	private String fileName;

	private byte[] bytecode;

	public GeneratedClass(String fileName, byte[] bytecode)
	{
		this.fileName = fileName;
		this.bytecode = bytecode;
	}

	public String getClassName()
	{
		return this.fileName;
	}

	public byte[] getBytecode()
	{
		return this.bytecode;
	}
}
