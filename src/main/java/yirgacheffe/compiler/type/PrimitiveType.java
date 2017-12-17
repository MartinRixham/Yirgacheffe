package yirgacheffe.compiler.type;

public enum PrimitiveType implements Type
{
	VOID("void", "V", "Void", 0),

	BOOL("bool", "Z", "Boolean", 1),

	CHAR("char", "C", "Character", 1),

	NUM("num", "D", "Double", 2);

	private String name;

	private String jvmType;

	private String fullyQualifiedType;

	private int width;

	PrimitiveType(String name, String jvmType, String wrapperClass, int width)
	{
		this.name = name;
		this.jvmType = jvmType;
		this.fullyQualifiedType = "java.lang." + wrapperClass;
		this.width = width;
	}

	@Override
	public Class<?> reflectionClass()
	{
		return null;
	}

	@Override
	public String toJVMType()
	{
		return this.jvmType;
	}

	@Override
	public String toFullyQualifiedType()
	{
		return this.fullyQualifiedType;
	}

	@Override
	public int width()
	{
		return this.width;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
