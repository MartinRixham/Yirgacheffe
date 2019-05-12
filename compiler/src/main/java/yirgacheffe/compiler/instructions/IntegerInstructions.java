package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;

public class IntegerInstructions implements Instructions
{
	public int getReturn()
	{
		return Opcodes.IRETURN;
	}

	public int getStore()
	{
		return Opcodes.ISTORE;
	}

	public int getLoad()
	{
		return Opcodes.ILOAD;
	}

	public int getTypeConversion()
	{
		return Opcodes.D2I;
	}

	public int getZero()
	{
		return Opcodes.ICONST_0;
	}
}
