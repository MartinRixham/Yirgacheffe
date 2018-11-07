package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;

public class DoubleInstructions implements Instructions
{
	@Override
	public int getReturn()
	{
		return Opcodes.DRETURN;
	}

	@Override
	public int getStore()
	{
		return Opcodes.DSTORE;
	}

	@Override
	public int getLoad()
	{
		return Opcodes.DLOAD;
	}

	@Override
	public int getZero()
	{
		return Opcodes.DCONST_0;
	}
}
