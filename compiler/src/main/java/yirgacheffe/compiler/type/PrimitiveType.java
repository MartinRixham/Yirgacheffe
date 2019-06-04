package yirgacheffe.compiler.type;

import yirgacheffe.compiler.instructions.DoubleInstructions;
import yirgacheffe.compiler.instructions.Instructions;
import yirgacheffe.compiler.instructions.IntegerInstructions;
import yirgacheffe.compiler.instructions.LongIntegerInstructions;
import yirgacheffe.compiler.instructions.VoidInstructions;

public enum PrimitiveType implements Type
{
	VOID(
		"Void", "V", 0, new VoidInstructions(),
		java.lang.Void.class, Float.NaN),

	BOOLEAN(
		"Bool", "Z", 1, new IntegerInstructions(),
		java.lang.Boolean.class, Float.NaN),

	CHAR(
		"Char", "C", 1, new IntegerInstructions(),
		java.lang.Character.class, 2),

	INT(
		"Num", "I", 1, new IntegerInstructions(),
		java.lang.Integer.class, 3),

	LONG(
		"Num", "J", 2, new LongIntegerInstructions(),
		java.lang.Long.class, 4),

	FLOAT(
		"Num", "F", 2, new DoubleInstructions(),
		java.lang.Float.class, 5),

	DOUBLE(
		"Num", "D", 2, new DoubleInstructions(),
		java.lang.Double.class, 6);

	private String name;

	private Class<?> reflectionClass;

	private String jvmType;

	private int width;

	private Instructions instructions;

	private float order;

	PrimitiveType(
		String name,
		String jvmType,
		int width,
		Instructions instructions,
		Class<?> reflectionClass,
		float order)
	{
		this.name = name;
		this.jvmType = jvmType;
		this.width = width;
		this.instructions = instructions;
		this.reflectionClass = reflectionClass;
		this.order = order;
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
		return this.reflectionClass.getName().replace('.', '/');
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

	public int convertTo(PrimitiveType type)
	{
		return this.instructions.convertTo(type);
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
		if (this == other)
		{
			return true;
		}
		else if (other instanceof PrimitiveType)
		{
			PrimitiveType otherPrimitive = (PrimitiveType) other;

			return this.order > 0 && otherPrimitive.order > 0;
		}
		else
		{
			return other.reflectionClass().isAssignableFrom(this.reflectionClass);
		}
	}

	public boolean hasParameter()
	{
		return false;
	}

	public String getSignature()
	{
		return this.jvmType;
	}

	public boolean isPrimitive()
	{
		return true;
	}

	public float order()
	{
		return this.order;
	}
}
