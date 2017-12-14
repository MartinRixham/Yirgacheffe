package yirgacheffe.compiler.type;

import yirgacheffe.parser.YirgacheffeParser;

public class ReferenceType implements Type
{
	private String fullyQualifiedName;

	public ReferenceType(YirgacheffeParser.TypeContext context)
	{
		this.fullyQualifiedName = context.getText();
	}

	public ReferenceType(YirgacheffeParser.FullyQualifiedTypeContext context)
	{
		this.fullyQualifiedName = context.getText();
	}

	public ReferenceType(String packageName, String identifier)
	{
		this.fullyQualifiedName = packageName + "." + identifier;
	}

	public String toJVMType()
	{
		return "L" + this.fullyQualifiedName.replace('.', '/')  + ";";
	}

	public String toFullyQualifiedType()
	{
		return this.fullyQualifiedName;
	}

	@Override
	public int width()
	{
		return 1;
	}
}
