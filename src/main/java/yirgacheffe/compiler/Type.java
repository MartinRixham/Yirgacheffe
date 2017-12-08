package yirgacheffe.compiler;

import yirgacheffe.parser.YirgacheffeParser;

public class Type
{
	private YirgacheffeParser.SimpleTypeContext simpleType;

	private YirgacheffeParser.FullyQualifiedTypeContext fullyQualifiedType;

	public Type(YirgacheffeParser.TypeContext context)
	{
		this.simpleType = context.simpleType();
		this.fullyQualifiedType = context.fullyQualifiedType();
	}

	public Type(YirgacheffeParser.FullyQualifiedTypeContext fullyQualifiedType)
	{
		this.fullyQualifiedType = fullyQualifiedType;
	}

	public String toJVMType()
	{
		if (this.simpleType == null)
		{
			String name = this.fullyQualifiedType.getText();

			return "L" + name.replace('.', '/')  + ";";
		}
		else
		{
			String name = this.simpleType.getText();

			if (name.equals("num"))
			{
				return "D";
			}
			else
			{
				return "Ljava/lang/" + name + ";";
			}
		}
	}
}
