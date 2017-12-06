package yirgacheffe.compiler;

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
		int line = token.getLine();
		int index = token.getCharPositionInLine();

		this.error = "line " + line + ":" + index + " " + message;
	}

	@Override
	public String toString()
	{
		return this.error;
	}
}
