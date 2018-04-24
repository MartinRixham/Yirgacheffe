package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class ArrayType implements Type
{
	private String jvmType;

	private String fullyQualifiedType;

	private Class<?> reflectionClass;

	public ArrayType(String name)
	{
		this.jvmType = name.replace(".", "/");
		this.fullyQualifiedType = name.substring(2).replace(";", "[]");

		try
		{
			this.reflectionClass = Class.forName(name);
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
		return 1;
	}

	@Override
	public int getReturnInstruction()
	{
		return Opcodes.ARETURN;
	}

	@Override
	public int getStoreInstruction()
	{
		return Opcodes.ASTORE;
	}

	@Override
	public int getLoadInstruction()
	{
		return Opcodes.ALOAD;
	}

	@Override
	public String toString()
	{
		return this.fullyQualifiedType;
	}
}
