package yirgacheffe.compiler.expression;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Type;

public interface Literal extends Expression
{
	Object getValue();

	Type getType();

	static Literal parse(Coordinate coordinate, String text)
	{
		if (text.startsWith("\""))
		{
			return new Streeng(coordinate, text);
		}
		else if (text.startsWith("'"))
		{
			return new Char(coordinate, text);
		}
		else if (text.equals("true") || text.equals("false"))
		{
			return new Bool(coordinate, text);
		}
		else
		{
			return new Num(coordinate, text);
		}
	}
}
