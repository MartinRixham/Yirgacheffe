package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class VariableType implements Type
{
	private String name;

	public VariableType(String name)
	{
		this.name = name;
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
		return "java.lang.Object";
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
		return other instanceof VariableType;
	}

	public boolean hasParameter()
	{
		return true;
	}

	public String getSignature()
	{
		return "T" + this.name + ";";
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
