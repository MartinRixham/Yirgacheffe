package yirgacheffe.compiler.type;

import java.util.Arrays;

public class PrimitiveType implements Type
{
	private String name;

	public PrimitiveType(String name)
	{
		this.name = name;
	}

	public static boolean isPrimitive(String name)
	{
		return Arrays.asList("void", "bool", "char", "num").contains(name);
	}

	@Override
	public String toJVMType()
	{
		switch (this.name)
		{
			case "void":
				return "V";
			case "bool":
				return "B";
			case "char":
				return "C";
			default:
				return "D";
		}
	}

	@Override
	public String toFullyQualifiedType()
	{
		return this.name;
	}
}
