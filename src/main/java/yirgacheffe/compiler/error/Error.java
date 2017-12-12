package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Error implements Comparable<Error>
{
	private int line;

	private int charPosition;

	private String message;

	public Error(ParserRuleContext context, String message)
	{
		this(context.getStart(), message);
	}

	public Error(Token token, String message)
	{
		this(token.getLine(), token.getCharPositionInLine(), message);
	}

	public Error(int line, int charPosition, String message)
	{
		this.line = line;
		this.charPosition = charPosition;
		this.message = message;
	}

	@Override
	public String toString()
	{
		return "line " + this.line + ":" + this.charPosition + " " + this.message;
	}

	@Override
	public int compareTo(Error error)
	{
		return Integer.compare(this.line, error.line);
	}
}
