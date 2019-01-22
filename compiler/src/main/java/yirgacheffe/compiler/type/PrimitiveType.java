package yirgacheffe.compiler.type;

import yirgacheffe.compiler.instructions.DoubleInstructions;
import yirgacheffe.compiler.instructions.Instructions;
import yirgacheffe.compiler.instructions.IntegerInstructions;
import yirgacheffe.compiler.instructions.VoidInstructions;

public enum PrimitiveType implements Type
{
	VOID("Void", "V", 0, new VoidInstructions(), java.lang.Void.class),

	BOOLEAN("Bool", "Z", 1, new IntegerInstructions(), java.lang.Boolean.class),

	CHAR("Char", "C", 1, new IntegerInstructions(), java.lang.Character.class),

	INT("Num", "I", 2, new IntegerInstructions(), java.lang.Integer.class),

	LONG("Num", "J", 2, new DoubleInstructions(), java.lang.Long.class),

	FLOAT("Num", "F", 2, new DoubleInstructions(), java.lang.Float.class),

	DOUBLE("Num", "D", 2, new DoubleInstructions(), java.lang.Double.class);

	private String name;

	private Class<?> reflectionClass;

	private String jvmType;

	private int width;

	private Instructions instructions;

	PrimitiveType(
		String name,
		String jvmType,
		int width,
		Instructions instructions,
		Class<?> reflectionClass)
	{
		this.name = name;
		this.jvmType = jvmType;
		this.width = width;
		this.instructions = instructions;
		this.reflectionClass = reflectionClass;
	}

	public Class<?> reflectionClass()
	{
		return this.reflectionClass;
	}

	public String toJVMType()
	{
		return this.jvmType;
	}

	public String toFullyQualifiedType()
	{
		return this.reflectionClass.getName();
	}

	public int width()
	{
		return this.width;
	}

	public int getReturnInstruction()
	{
		return this.instructions.getReturn();
	}

	public int getStoreInstruction()
	{
		return this.instructions.getStore();
	}

	public int getLoadInstruction()
	{
		return this.instructions.getLoad();
	}

	public int getZero()
	{
		return this.instructions.getZero();
	}

	public String toString()
	{
		return this.name;
	}

	public boolean isAssignableTo(Type other)
	{
		return this == other ||
			other.reflectionClass().isAssignableFrom(this.reflectionClass);
	}
}
