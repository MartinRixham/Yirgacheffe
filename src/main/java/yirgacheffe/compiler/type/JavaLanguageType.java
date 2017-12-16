package yirgacheffe.compiler.type;

public class JavaLanguageType implements Type
{
	private Class<?> reflectionClass;

	private String identifier;

	public JavaLanguageType(String identifier)
	{
		try
		{
			this.reflectionClass =
				Thread.currentThread()
					.getContextClassLoader()
					.loadClass("java.lang." + identifier);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}

		this.identifier = identifier;
	}

	@Override
	public Class<?> reflectionClass()
	{
		return this.reflectionClass;
	}

	@Override
	public String toJVMType()
	{
		return "Ljava/lang/" + this.identifier + ";";
	}

	@Override
	public String toFullyQualifiedType()
	{
		return "java.lang." + this.identifier;
	}

	@Override
	public int width()
	{
		return 1;
	}
}
