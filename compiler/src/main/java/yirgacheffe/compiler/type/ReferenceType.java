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
		return "L" + this.toFullyQualifiedType() + ";";
	}

	public String toFullyQualifiedType()
	{
		return this.reflectionClass.getName().replace('.', '/');
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

	public String toString()
	{
		return this.reflectionClass.getName();
	}

	public boolean isAssignableTo(Type other)
	{
		return other.reflectionClass().isAssignableFrom(this.reflectionClass());
	}

	public boolean hasParameter()
	{
		return false;
	}

	public String getSignature()
	{
		return this.toJVMType();
	}

	public boolean isPrimitive()
	{
		return false;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ReferenceType)
		{
			ReferenceType referenceType = (ReferenceType) other;

			return this.reflectionClass.equals(referenceType.reflectionClass);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return this.reflectionClass.hashCode();
	}
}
