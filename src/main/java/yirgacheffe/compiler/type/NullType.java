package yirgacheffe.compiler.type;

public class NullType implements Type
{
	public String toJVMType()
	{
		return "V";
	}

	public String toFullyQualifiedType()
	{
		return "void";
	}

	@Override
	public int width()
	{
		return 1;
	}
}
