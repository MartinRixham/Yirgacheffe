package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import yirgacheffe.lang.Array;

public class ParseErrorStrategy extends DefaultErrorStrategy
{
	@Override
	public void reportInputMismatch(Parser recognizer, InputMismatchException e)
	{
		if (this.getTokens(recognizer).contains("'}'"))
		{
			String message = "Missing '}'";

			recognizer.notifyErrorListeners(e.getOffendingToken(), message, e);
		}
		else
		{
			super.reportInputMismatch(recognizer, e);
		}
	}

	@Override
	public void reportUnwantedToken(Parser recognizer)
	{
		if (this.getTokens(recognizer).contains("'}'"))
		{
			String message = "Missing '}'";

			recognizer.notifyErrorListeners(recognizer.getCurrentToken(), message, null);
		}
		else
		{
			super.reportUnwantedToken(recognizer);
		}
	}

	private Array<String> getTokens(Parser recognizer)
	{
		String expectedTokens =
			recognizer.getExpectedTokens().toString(recognizer.getVocabulary());

		String[] tokens =
			expectedTokens
				.substring(1, expectedTokens.length() - 1)
				.split(", ");

		return new Array<>(tokens);
	}

	@Override
	public void reportNoViableAlternative(Parser recognizer, NoViableAltException e)
	{
		String keyword = e.getOffendingToken().getText();
		String message = "Invalid use of symbol '" + keyword + "'";

		recognizer.notifyErrorListeners(e.getOffendingToken(), message, e);
	}
}
