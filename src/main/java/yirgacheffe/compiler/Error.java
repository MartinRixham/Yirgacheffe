package yirgacheffe.compiler;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Error
{
	private String error;

	public Error(ParserRuleContext context, String message)
	{
		Token start = context.getStart();
		int line = start.getLine();
		int index = start.getCharPositionInLine();

		this.error = "line " + line + ":" + index + " " + message;
	}

	@Override
	public String toString()
	{
		return this.error;
	}
}
