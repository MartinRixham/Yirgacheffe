package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public enum PrimitiveType implements Type
{
	VOID(
		"Void", "V", 0, Opcodes.RETURN, Opcodes.ASTORE,
		Opcodes.ALOAD, java.lang.Void.class),

	BOOLEAN(
		"Bool", "Z", 1, Opcodes.IRETURN, Opcodes.ISTORE,
		Opcodes.ILOAD, java.lang.Boolean.class),

	CHAR(
		"Char", "C", 1, Opcodes.IRETURN, Opcodes.ISTORE,
		Opcodes.ILOAD, java.lang.Character.class),

	INT(
		"Num", "I", 2, Opcodes.DRETURN, Opcodes.DSTORE,
		Opcodes.DLOAD, java.lang.Double.class),

	LONG(
		"Num", "J", 2, Opcodes.DRETURN, Opcodes.DSTORE,
		Opcodes.DLOAD, java.lang.Double.class),

	FLOAT(
		"Num", "F", 2, Opcodes.DRETURN, Opcodes.DSTORE,
		Opcodes.DLOAD, java.lang.Double.class),

	DOUBLE(
		"Num", "D", 2, Opcodes.DRETURN, Opcodes.DSTORE,
		Opcodes.DLOAD, java.lang.Double.class);

	private String name;

	private Class<?> reflectionClass;

	private String jvmType;

	private int width;

	private int returnInstruction;

	private int storeInstruction;

	private int loadInstruction;

	PrimitiveType(
		String name,
		String jvmType,
		int width,
		int returnInstruction,
		int storeInstruction,
		int loadInstruction,
		Class<?> reflectionClass)
	{
		this.name = name;
		this.jvmType = jvmType;
		this.width = width;
		this.returnInstruction = returnInstruction;
		this.storeInstruction = storeInstruction;
		this.loadInstruction = loadInstruction;
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
		return this.returnInstruction;
	}

	public int getStoreInstruction()
	{
		return this.storeInstruction;
	}

	public int getLoadInstruction()
	{
		return this.loadInstruction;
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
