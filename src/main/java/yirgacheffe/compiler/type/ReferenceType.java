package yirgacheffe.compiler.type;

public class ReferenceType implements Type
{
	private Class<?> reflectionClass;

	public ReferenceType(Class<?> reflectionClass)
	{
		this.reflectionClass = reflectionClass;
	}

	@Override
	public Class<?> reflectionClass()
	{
		return this.reflectionClass;
	}

	public String toJVMType()
	{
		return "L" + this.toFullyQualifiedType().replace('.', '/')  + ";";
	}

	public String toFullyQualifiedType()
	{
		return this.reflectionClass.getName();
	}

	@Override
	public int width()
	{
		return 1;
	}
}
