package yirgacheffe.compiler.type;

public class GenericType implements Type
{
	private Type type;

	public GenericType(Type type)
	{
		this.type = type;
	}

	@Override
	public Class<?> reflectionClass()
	{
		return this.type.reflectionClass();
	}

	@Override
	public String toJVMType()
	{
		return "Ljava/lang/Object;";
	}

	@Override
	public String toFullyQualifiedType()
	{
		return this.type.toFullyQualifiedType();
	}

	@Override
	public int width()
	{
		return this.type.width();
	}

	@Override
	public int getReturnInstruction()
	{
		return this.type.getReturnInstruction();
	}

	@Override
	public int getStoreInstruction()
	{
		return this.type.getStoreInstruction();
	}

	@Override
	public int getLoadInstruction()
	{
		return this.type.getLoadInstruction();
	}

	@Override
	public String toString()
	{
		return this.type.toString();
	}

	@Override
	public boolean isAssignableTo(Type other)
	{
		return this.type.isAssignableTo(other);
	}
}
