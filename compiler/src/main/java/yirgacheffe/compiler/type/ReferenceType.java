package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class ReferenceType implements Type
{
	private Class<?> reflectionClass;

	public ReferenceType(Class<?> reflectionClass)
	{
		this.reflectionClass = reflectionClass;
	}

	@Override
	public Class<?> reflectionClass()
	{
		return this.reflectionClass;
	}

	public String toJVMType()
	{
		return "L" + this.toFullyQualifiedType().replace('.', '/')  + ";";
	}

	public String toFullyQualifiedType()
	{
		return this.reflectionClass.getName();
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
	public String toString()
	{
		return this.toFullyQualifiedType();
	}
}
