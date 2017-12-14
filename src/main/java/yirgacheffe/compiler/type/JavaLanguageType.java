package yirgacheffe.compiler.type;

public class JavaLanguageType implements Type
{
	private String identifier;

	public JavaLanguageType(String identifier)
	{
		this.identifier = identifier;
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
