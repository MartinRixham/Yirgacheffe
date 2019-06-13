package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;

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

	public int getArrayStore()
	{
		return Opcodes.IASTORE;
	}

	public int getLoad()
	{
		return Opcodes.ILOAD;
	}

	public int convertTo(PrimitiveType type)
	{
		if (type == PrimitiveType.LONG)
		{
			return Opcodes.I2L;
		}
		else
		{
			return Opcodes.I2D;
		}
	}

	public int getZero()
	{
		return Opcodes.ICONST_0;
	}

	public int getType()
	{
		return Opcodes.T_INT;
	}
}
