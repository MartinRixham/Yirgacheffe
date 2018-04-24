package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class StringType implements Type
{
	private String fullyQualifiedType = "java.lang.String";

	public StringType()
	{
	}

	@Override
	public Class<?> reflectionClass()
	{
		return String.class;
	}

	@Override
	public String toJVMType()
	{
		return "Ljava/lang/String;";
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
