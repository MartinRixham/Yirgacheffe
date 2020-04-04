package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;

import java.util.Arrays;

public class ParseErrorStrategy extends DefaultErrorStrategy
{
	@Override
	public void reportInputMismatch(Parser recognizer, InputMismatchException e)
	{
		String expectedTokens =
			e.getExpectedTokens().toString(recognizer.getVocabulary());

		String[] tokens =
			expectedTokens
				.substring(1, expectedTokens.length() - 1)
				.split(", ");

		if (Arrays.asList(tokens).contains("'}'"))
		{
			String message = "Missing '}'";

			recognizer.notifyErrorListeners(e.getOffendingToken(), message, e);
		}
		else
		{
			super.reportInputMismatch(recognizer, e);
		}
	}

	/*@Override
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
