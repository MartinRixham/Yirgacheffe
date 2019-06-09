package yirgacheffe.compiler.type;

public class IntersectionType implements Type
{
	private Type firstType;

	private Type secondType;

	public IntersectionType(Type firstType, Type secondType)
	{
		this.firstType = firstType;
		this.secondType = secondType;
	}

	public Class<?> reflectionClass()
	{
		return this.firstType.reflectionClass();
	}

	public String toJVMType()
	{
		return this.firstType.toJVMType();
	}

	public String toFullyQualifiedType()
	{
		return this.firstType.toFullyQualifiedType();
	}

	public int width()
	{
		if (!this.firstType.isPrimitive() || !this.secondType.isPrimitive())
		{
			return 1;
		}
		else if ((this.firstType.width() == 2 ||
				this.secondType.width() == 2) &&
			this.firstType.isAssignableTo(this.secondType))
		{
			return 2;
		}
		else
		{
			return 1;
		}
	}

	public int getReturnInstruction()
	{
		return this.firstType.getReturnInstruction();
	}

	public int getStoreInstruction()
	{
		return this.firstType.getStoreInstruction();
	}

	public int getLoadInstruction()
	{
		return this.firstType.getLoadInstruction();
	}

	public int getZero()
	{
		return this.firstType.getZero();
	}

	public String toString()
	{
		return this.firstType.toString();
	}

	public boolean isAssignableTo(Type other)
	{
		return this.firstType.isAssignableTo(other) &&
			this.secondType.isAssignableTo(other);
	}

	public boolean hasParameter()
	{
		return this.firstType.hasParameter();
	}

	public String getSignature()
	{
		return this.firstType.getSignature();
	}

	public boolean isPrimitive()
	{
		return this.firstType.isPrimitive() &&
			this.firstType.isAssignableTo(this.secondType);
	}

	@Override
	public boolean equals(Object other)
	{
		return this.secondType.equals(other);
	}

	@Override
	public int hashCode()
	{
		return this.secondType.hashCode();
	}
}
