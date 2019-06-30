package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

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

	public int getArrayStore()
	{
		return Opcodes.LASTORE;
	}

	public int getLoad()
	{
		return Opcodes.LLOAD;
	}

	public int convertTo(Type type)
	{
		if (type.equals(PrimitiveType.INT))
		{
			return Opcodes.L2I;
		}
		else if (type.equals(PrimitiveType.DOUBLE))
		{
			return Opcodes.L2D;
		}
		else
		{
			return Opcodes.NOP;
		}
	}

	public int getZero()
	{
		return Opcodes.LCONST_0;
	}

	public int getType()
	{
		return Opcodes.T_LONG;
	}
}
