package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public enum PrimitiveType implements Type
{
	VOID("void", "V", "Void", 0, Opcodes.RETURN, Opcodes.ASTORE),

	BOOL("bool", "Z", "Boolean", 1, Opcodes.IRETURN, Opcodes.ISTORE),

	CHAR("char", "C", "Character", 1, Opcodes.IRETURN, Opcodes.ISTORE),

	DOUBLE("num", "D", "Double", 2, Opcodes.DRETURN, Opcodes.DSTORE);

	private String name;

	private Class<?> reflectionClass;

	private String jvmType;

	private String fullyQualifiedType;

	private int width;

	private int returnInstruction;

	private int storeInstruction;

	PrimitiveType(
		String name,
		String jvmType,
		String wrapperClass,
		int width,
		int returnInstruction,
		int storeInstruction)
	{
		this.name = name;
		this.jvmType = jvmType;
		this.fullyQualifiedType = "java.lang." + wrapperClass;
		this.width = width;
		this.returnInstruction = returnInstruction;
		this.storeInstruction = storeInstruction;

		try
		{
			this.reflectionClass =
				Thread.currentThread()
					.getContextClassLoader()
					.loadClass(this.fullyQualifiedType);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
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
		return this.fullyQualifiedType;
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
	public String toString()
	{
		return this.name;
	}
}
