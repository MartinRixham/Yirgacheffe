package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;

public class VoidInstructions implements Instructions
{
	public int getReturn()
	{
		return Opcodes.RETURN;
	}

	public int getStore()
	{
		return Opcodes.NOP;
	}

	public int getLoad()
	{
		return Opcodes.NOP;
	}

	public int getTypeConversion()
	{
		return Opcodes.NOP;
	}

	public int getZero()
	{
		return Opcodes.NOP;
	}
}
