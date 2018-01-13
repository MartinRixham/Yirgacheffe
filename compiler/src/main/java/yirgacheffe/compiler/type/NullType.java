package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class NullType implements Type
{
	@Override
	public Class<?> reflectionClass()
	{
		throw new RuntimeException();
	}

	public String toJVMType()
	{
		return "I";
	}

	public String toFullyQualifiedType()
	{
		return "java.lang.Integer";
	}

	@Override
	public int width()
	{
		return 1;
	}

	@Override
	public int getReturnInstruction()
	{
		return Opcodes.IRETURN;
	}

	@Override
	public int getStoreInstruction()
	{
		return Opcodes.ISTORE;
	}
}
