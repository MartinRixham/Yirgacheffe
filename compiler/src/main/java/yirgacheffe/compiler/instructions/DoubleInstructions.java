package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;

public class DoubleInstructions implements Instructions
{
	public int getReturn()
	{
		return Opcodes.DRETURN;
	}

	public int getStore()
	{
		return Opcodes.DSTORE;
	}

	public int getLoad()
	{
		return Opcodes.DLOAD;
	}

	public int getTypeConversion()
	{
		return Opcodes.NOP;
	}

	public int getZero()
	{
		return Opcodes.DCONST_0;
	}
}
