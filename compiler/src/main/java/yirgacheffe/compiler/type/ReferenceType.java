package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class ReferenceType implements Type
{
	private Class<?> reflectionClass;

	public ReferenceType(Class<?> reflectionClass)
	{
		this.reflectionClass = reflectionClass;
	}

	public Class<?> reflectionClass()
	{
		return this.reflectionClass;
	}

	public String toJVMType()
	{
		return "L" + this.toFullyQualifiedType().replace('.', '/') + ";";
	}

	public String toFullyQualifiedType()
	{
		return this.reflectionClass.getName();
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

	public String toString()
	{
		return this.toFullyQualifiedType();
	}

	public boolean isAssignableTo(Type other)
	{
		return other.reflectionClass().isAssignableFrom(this.reflectionClass());
	}
}
