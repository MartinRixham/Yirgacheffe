package yirgacheffe.compiler;

import yirgacheffe.parser.YirgacheffeParser;

public class ImportedType implements Type
{
	private YirgacheffeParser.SimpleTypeContext simpleType;

	private YirgacheffeParser.FullyQualifiedTypeContext fullyQualifiedType;

	public ImportedType(YirgacheffeParser.TypeContext context)
	{
		this.simpleType = context.simpleType();
		this.fullyQualifiedType = context.fullyQualifiedType();
	}

	public ImportedType(YirgacheffeParser.FullyQualifiedTypeContext fullyQualifiedType)
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

			switch (name)
			{
				case "void":
					return "V";
				case "bool":
					return "B";
				case "char":
					return "C";
				case "num":
					return "D";
				default:
					return "Ljava/lang/" + name + ";";
			}
		}
	}
}
