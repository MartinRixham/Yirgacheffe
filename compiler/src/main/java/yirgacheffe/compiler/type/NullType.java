package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class NullType implements Type
{
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

	public boolean isAssignableTo(Type other)
	{
		return true;
	}
}
