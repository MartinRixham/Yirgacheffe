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

	@Override
	public int width()
	{
		if (this.name.equals("num"))
		{
			return 2;
		}
		else
		{
			return 1;
		}
	}

	@Override
	public boolean equals(Object other)
	{
		return this.name.equals(((PrimitiveType) other).name);
	}

	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}
}
