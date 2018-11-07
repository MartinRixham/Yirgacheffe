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
		return this.firstType.width();
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
}
