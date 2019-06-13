package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class NullType implements Type
{
	private String name;

	public NullType(String name)
	{
		this.name = name;
	}

	public NullType()
	{
		this.name = "java.lang.Object";
	}

	public Class<?> reflectionClass()
	{
		return Object.class;
	}

	public String toJVMType()
	{
		return "Ljava/lang/Object;";
	}

	public String toFullyQualifiedType()
	{
		return this.name;
	}

	public int width()
	{
		return 1;
	}

	public int getReturnInstruction()
	{
		return Opcodes.ARETURN;
	}

	public int getStoreInstruction()
	{
		return Opcodes.ASTORE;
	}

	public int getArrayStoreInstruction()
	{
		return Opcodes.AASTORE;
	}

	public int getLoadInstruction()
	{
		return Opcodes.ALOAD;
	}

	public int getZero()
	{
		return Opcodes.ACONST_NULL;
	}

	public boolean isAssignableTo(Type other)
	{
		return true;
	}

	public boolean hasParameter()
	{
		return false;
	}

	public String getSignature()
	{
		return null;
	}

	public boolean isPrimitive()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
