package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Error
{
	private String error;

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
		this.error = "line " + line + ":" + charPosition + " " + message;
	}

	@Override
	public String toString()
	{
		return this.error;
	}
}
