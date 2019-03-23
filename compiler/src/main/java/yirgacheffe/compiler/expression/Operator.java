package yirgacheffe.compiler.expression;

import org.objectweb.asm.Opcodes;

public enum Operator
{
	ADD(Opcodes.DADD, "add"),

	SUBTRACT(Opcodes.DSUB, "subtract"),

	MULTIPLY(Opcodes.DMUL, "multiply"),

	DIVIDE(Opcodes.DDIV, "divide"),

	REMAINDER(Opcodes.DREM, "find remainder of");

	private int opcode;

	private String description;

	Operator(int opcode, String description)
	{
		this.opcode = opcode;
		this.description = description;
	}

	public int getOpcode()
	{
		return this.opcode;
	}

	public String getDescription()
	{
		return this.description;
	}
}
