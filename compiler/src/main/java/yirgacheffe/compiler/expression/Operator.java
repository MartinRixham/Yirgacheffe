package yirgacheffe.compiler.expression;

import org.objectweb.asm.Opcodes;

public enum Operator
{
	ADD(Opcodes.IADD, Opcodes.DADD, "add"),

	SUBTRACT(Opcodes.ISUB, Opcodes.DSUB, "subtract"),

	MULTIPLY(Opcodes.IMUL, Opcodes.DMUL, "multiply"),

	DIVIDE(Opcodes.IDIV, Opcodes.DDIV, "divide"),

	REMAINDER(Opcodes.IREM, Opcodes.DREM, "find remainder of");

	private int intOpcode;

	private int doubleOpcode;

	private String description;

	Operator(int intOpcode, int doubleOpcode, String description)
	{
		this.intOpcode = intOpcode;
		this.doubleOpcode = doubleOpcode;
		this.description = description;
	}

	public int getIntOpcode()
	{
		return this.intOpcode;
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
