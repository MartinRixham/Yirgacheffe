package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class NullType implements Type
{
	@Override
	public Class<?> reflectionClass()
	{
		return Object.class;
	}

	@Override
	public String toJVMType()
	{
		return "Ljava/lang/Object;";
	}

	@Override
	public String toFullyQualifiedType()
	{
		return "java.lang.Object";
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
	public boolean isAssignableTo(Type other)
	{
		return true;
	}
}
