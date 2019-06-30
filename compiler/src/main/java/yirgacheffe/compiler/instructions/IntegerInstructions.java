package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

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

	public int convertTo(Type type)
	{
		if (type.equals(PrimitiveType.LONG))
		{
			return Opcodes.I2L;
		}
		else if (type.equals(PrimitiveType.DOUBLE))
		{
			return Opcodes.I2D;
		}
		else
		{
			return Opcodes.NOP;
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
