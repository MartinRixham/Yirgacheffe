package yirgacheffe.compiler.type;

public enum PrimitiveType implements Type
{
	VOID("V", "Void", 0),

	BOOL("B", "Boolean", 1),

	CHAR("C", "Character", 1),

	NUM("D", "Double", 2);

	private Class<?> reflectionClass;

	private String jvmType;

	private String fullyQualifiedType;

	private int width;

	PrimitiveType(String jvmType, String wrapperClass, int width)
	{
		this.jvmType = jvmType;
		this.fullyQualifiedType = "java.lang." + wrapperClass;
		this.width = width;

		try
		{
			this.reflectionClass =
				Thread.currentThread()
					.getContextClassLoader()
					.loadClass(this.fullyQualifiedType);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
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
}
