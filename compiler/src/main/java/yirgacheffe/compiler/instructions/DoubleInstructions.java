package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

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

	public int convertTo(Type type)
	{
		if (type.equals(PrimitiveType.INT))
		{
			return Opcodes.D2I;
		}
		else if (type.equals(PrimitiveType.LONG))
		{
			return Opcodes.D2L;
		}
		else
		{
			return Opcodes.NOP;
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
