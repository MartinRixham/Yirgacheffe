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
		"Num", "L", 2, Opcodes.DRETURN, Opcodes.DSTORE,
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

	@Override
	public Class<?> reflectionClass()
	{
		return this.reflectionClass;
	}

	@Override
	public String toJVMType()
	{
		return this.jvmType;
	}

	@Override
	public String toFullyQualifiedType()
	{
		return this.reflectionClass.getName();
	}

	@Override
	public int width()
	{
		return this.width;
	}

	@Override
	public int getReturnInstruction()
	{
		return this.returnInstruction;
	}

	@Override
	public int getStoreInstruction()
	{
		return this.storeInstruction;
	}

	@Override
	public int getLoadInstruction()
	{
		return this.loadInstruction;
	}

	@Override
	public String toString()
	{
		return this.name;
	}

	@Override
	public boolean isAssignableTo(Type other)
	{
		return this == other ||
			other.reflectionClass().isAssignableFrom(this.reflectionClass);
	}
}
