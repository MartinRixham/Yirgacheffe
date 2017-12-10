package yirgacheffe.compiler.Type;

public class NullType implements Type
{
	public String toJVMType()
	{
		return "null";
	}
}
