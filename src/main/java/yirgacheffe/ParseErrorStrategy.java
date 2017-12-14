package yirgacheffe;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;

public class ParseErrorStrategy extends DefaultErrorStrategy
{
	@Override
	public void reportInputMismatch(Parser recognizer, InputMismatchException e)
		throws RecognitionException
	{
		super.reportInputMismatch(recognizer, e);
	}

	@Override
	public void reportMissingToken(Parser recognizer)
	{
		super.reportMissingToken(recognizer);
	}
}
