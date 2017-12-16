package yirgacheffe.compiler.type;

public class NullType implements Type
{
	@Override
	public Class<?> reflectionClass()
	{
		throw new RuntimeException();
	}

	public String toJVMType()
	{
		return "I";
	}

	public String toFullyQualifiedType()
	{
		return "java.lang.Integer";
	}

	@Override
	public int width()
	{
		return 1;
	}
}
