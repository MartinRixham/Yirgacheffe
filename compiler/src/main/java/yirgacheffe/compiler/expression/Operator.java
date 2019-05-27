package yirgacheffe.compiler.expression;

import org.objectweb.asm.Opcodes;

public enum Operator
{
	ADD(Opcodes.IADD, Opcodes.LADD, Opcodes.DADD, "add"),

	SUBTRACT(Opcodes.ISUB, Opcodes.LSUB, Opcodes.DSUB, "subtract"),

	MULTIPLY(Opcodes.IMUL, Opcodes.LMUL, Opcodes.DMUL, "multiply"),

	DIVIDE(Opcodes.IDIV, Opcodes.LDIV, Opcodes.DDIV, "divide"),

	REMAINDER(Opcodes.IREM, Opcodes.LREM, Opcodes.DREM, "find remainder of");

	private int intOpcode;

	private int longOpcode;

	private int doubleOpcode;

	private String description;

	Operator(int intOpcode, int longOpcode, int doubleOpcode, String description)
	{
		this.intOpcode = intOpcode;
		this.longOpcode = longOpcode;
		this.doubleOpcode = doubleOpcode;
		this.description = description;
	}

	public int getIntOpcode()
	{
		return this.intOpcode;
	}

	public int getLongOpcode()
	{
		return this.longOpcode;
	}

	public int getDoubleOpcode()
	{
		return this.doubleOpcode;
	}

	public String getDescription()
	{
		return this.description;
	}
}
