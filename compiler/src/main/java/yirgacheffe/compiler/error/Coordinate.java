package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Coordinate implements Comparable<Coordinate>
{
	private int line;

	private int charPosition;

	public Coordinate(int line, int charPosition)
	{
		this.line = line;
		this.charPosition = charPosition;
	}

	public Coordinate(Token token)
	{
		this.line = token.getLine();
		this.charPosition = token.getCharPositionInLine();
	}

	public Coordinate(ParserRuleContext context)
	{
		this(context.getStart());
	}

	@Override
	public String toString()
	{
		return "line " + this.line + ":" + this.charPosition;
	}

	@Override
	public int compareTo(Coordinate other)
	{
		return Integer.compare(this.line, other.line);
	}
}
