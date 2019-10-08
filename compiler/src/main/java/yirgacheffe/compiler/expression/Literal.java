package yirgacheffe.compiler.expression;

import yirgacheffe.compiler.type.Type;

public interface Literal extends Expression
{
	Object getValue();

	Type getType();

	static Literal parse(String text)
	{
		if (text.startsWith("\""))
		{
			return new Streeng(text);
		}
		else if (text.startsWith("'"))
		{
			return new Char(text);
		}
		else if (text.equals("true") || text.equals("false"))
		{
			return new Bool(text);
		}
		else
		{
			return new Num(text);
		}
	}
}
