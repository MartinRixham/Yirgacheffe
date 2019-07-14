package yirgacheffe.compiler.instructions;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class FloatInstructions implements Instructions
{
	public int getReturn()
	{
		return Opcodes.FRETURN;
	}

	public int getStore()
	{
		return Opcodes.FSTORE;
	}

	public int getArrayStore()
	{
		return Opcodes.FASTORE;
	}

	public int getLoad()
	{
		return Opcodes.FLOAD;
	}

	public int convertTo(Type type)
	{
		if (type.equals(PrimitiveType.INT))
		{
			return Opcodes.F2I;
		}
		else if (type.equals(PrimitiveType.LONG))
		{
			return Opcodes.F2L;
		}
		else if (type.equals(PrimitiveType.DOUBLE))
		{
			return Opcodes.F2D;
		}
		else
		{
			return Opcodes.NOP;
		}
	}

	public int getZero()
	{
		return Opcodes.FCONST_0;
	}

	public int getType()
	{
		return Opcodes.T_FLOAT;
	}
}
