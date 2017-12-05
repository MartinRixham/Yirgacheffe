package yirgacheffe;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class Block
{
	private String[] sourceTokens;

	public Block(String source)
	{
		this.sourceTokens = source.split(";|\\s+");
	}

	public void compile(ClassWriter writer)
	{
		writer.visitField(Opcodes.ACC_PRIVATE, this.sourceTokens[1], "I", null, null);
	}
}
