package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;

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

	public int getArrayStore()
	{
		return Opcodes.NOP;
	}

	public int getLoad()
	{
		return Opcodes.NOP;
	}

	public int convertTo(PrimitiveType type)
	{
		return Opcodes.NOP;
	}

	public int getZero()
	{
		return Opcodes.NOP;
	}

	public int getType()
	{
		return Opcodes.NOP;
	}
}
