package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;

public class VoidInstructions implements Instructions
{
	@Override
	public int getReturn()
	{
		return Opcodes.RETURN;
	}

	@Override
	public int getStore()
	{
		return Opcodes.NOP;
	}

	@Override
	public int getLoad()
	{
		return Opcodes.NOP;
	}

	@Override
	public int getZero()
	{
		return Opcodes.NOP;
	}
}
