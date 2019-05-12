package yirgacheffe.compiler.type;

public class VariableType implements Type
{
	private String name;

	public VariableType(String name)
	{
		this.name = name;
	}

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
		return this.name;
	}

	public int width()
	{
		return 1;
	}

	public int getReturnInstruction()
	{
		return 0;
	}

	public int getStoreInstruction()
	{
		return 0;
	}

	public int getLoadInstruction()
	{
		return 0;
	}

	public int getZero()
	{
		return 0;
	}

	public boolean isAssignableTo(Type other)
	{
		return false;
	}

	public boolean hasParameter()
	{
		return true;
	}

	public String getSignature()
	{
		return "T" + this.name + ";";
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
