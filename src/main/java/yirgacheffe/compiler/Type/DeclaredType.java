package yirgacheffe.compiler.Type;

public class DeclaredType implements Type
{
	private String packageName;

	private String identifier;

	public DeclaredType(String packageName, String identifier)
	{
		this.packageName = packageName;
		this.identifier = identifier;
	}

	public String toJVMType()
	{
		return "L" + this.packageName.replace('.', '/') + "/" +
			this.identifier + ";";
	}

	public String toFullyQualifiedType()
	{
		return this.packageName + "." + this.identifier;
	}
}
