package yirgacheffe.compiler.Type;

public class NullType implements Type
{
	public String toJVMType()
	{
		return "V";
	}

	public String toFullyQualifiedType()
	{
		throw new RuntimeException();
	}
}
