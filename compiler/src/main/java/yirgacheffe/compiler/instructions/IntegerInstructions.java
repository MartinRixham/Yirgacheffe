package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;

public class IntegerInstructions implements Instructions
{
	@Override
	public int getReturn()
	{
		return Opcodes.IRETURN;
	}

	@Override
	public int getStore()
	{
		return Opcodes.ISTORE;
	}

	@Override
	public int getLoad()
	{
		return Opcodes.ILOAD;
	}

	@Override
	public int getZero()
	{
		return Opcodes.ICONST_0;
	}
}
