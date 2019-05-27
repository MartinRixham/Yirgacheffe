package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;

public class LongIntegerInstructions implements Instructions
{
	public int getReturn()
	{
		return Opcodes.LRETURN;
	}

	public int getStore()
	{
		return Opcodes.LSTORE;
	}

	public int getLoad()
	{
		return Opcodes.LLOAD;
	}

	public int convertTo(PrimitiveType type)
	{
		if (type == PrimitiveType.INT)
		{
			return Opcodes.L2I;
		}
		else
		{
			return Opcodes.L2D;
		}
	}

	public int getZero()
	{
		return Opcodes.LCONST_0;
	}
}
