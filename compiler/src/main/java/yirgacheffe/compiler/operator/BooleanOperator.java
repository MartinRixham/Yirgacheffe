package yirgacheffe.compiler.operator;

import org.objectweb.asm.Opcodes;

public enum BooleanOperator
{
	AND(Opcodes.IFEQ, Opcodes.IFNULL),

	OR(Opcodes.IFNE, Opcodes.IFNONNULL);

	private int integerOpcode;

	private int referenceOpcode;

	BooleanOperator(int integerOpcode, int referenceOpcode)
	{
		this.integerOpcode = integerOpcode;
		this.referenceOpcode = referenceOpcode;
	}

	public int integerOpcode()
	{
		return this.integerOpcode;
	}

	public int referenceOpcode()
	{
		return this.referenceOpcode;
	}
}
