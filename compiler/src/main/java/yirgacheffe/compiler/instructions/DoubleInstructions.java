package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;

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

	public int getArrayStore()
	{
		return Opcodes.DASTORE;
	}

	public int getLoad()
	{
		return Opcodes.DLOAD;
	}

	public int convertTo(PrimitiveType type)
	{
		if (type == PrimitiveType.INT)
		{
			return Opcodes.D2I;
		}
		else
		{
			return Opcodes.D2L;
		}
	}

	public int getZero()
	{
		return Opcodes.DCONST_0;
	}

	public int getType()
	{
		return Opcodes.T_DOUBLE;
	}
}
