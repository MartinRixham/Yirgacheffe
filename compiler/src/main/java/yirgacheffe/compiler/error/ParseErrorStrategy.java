package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;

public class ParseErrorStrategy extends DefaultErrorStrategy
{
	/*@Override
	public void reportInputMismatch(Parser recognizer, InputMismatchException e)
	{
		super.reportInputMismatch(recognizer, e);
	}

	@Override
	public void reportMissingToken(Parser recognizer)
	{
		super.reportMissingToken(recognizer);
	}*/

	@Override
	public void reportNoViableAlternative(Parser recognizer, NoViableAltException e)
	{
		String keyword = e.getOffendingToken().getText();
		String message = "Invalid use of symbol '" + keyword + "'";

		recognizer.notifyErrorListeners(e.getOffendingToken(), message, e);
	}
}
