package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public enum PrimitiveType implements Type
{
	VOID("void", "V", "Void", 0, Opcodes.RETURN, Opcodes.ASTORE, Opcodes.ALOAD),

	BOOL("bool", "Z", "Boolean", 1, Opcodes.IRETURN, Opcodes.ISTORE, Opcodes.ILOAD),

	CHAR("char", "C", "Character", 1, Opcodes.IRETURN, Opcodes.ISTORE, Opcodes.ILOAD),

	DOUBLE("num", "D", "Double", 2, Opcodes.DRETURN, Opcodes.DSTORE, Opcodes.DLOAD);

	private String name;

	private Class<?> reflectionClass;

	private String jvmType;

	private String fullyQualifiedType;

	private int width;

	private int returnInstruction;

	private int storeInstruction;

	private int loadInstruction;

	PrimitiveType(
		String name,
		String jvmType,
		String wrapperClass,
		int width,
		int returnInstruction,
		int storeInstruction,
		int loadInstruction)
	{
		this.name = name;
		this.jvmType = jvmType;
		this.fullyQualifiedType = "java.lang." + wrapperClass;
		this.width = width;
		this.returnInstruction = returnInstruction;
		this.storeInstruction = storeInstruction;
		this.loadInstruction = loadInstruction;

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
	public int getLoadInstruction()
	{
		return this.loadInstruction;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
