package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import yirgacheffe.lang.Array;

public class ParseErrorListener extends BaseErrorListener
{
	private Array<Error> errors;

	public ParseErrorListener(Array<Error> errors)
	{
		this.errors = errors;
	}

	@Override
	public void syntaxError(
		Recognizer<?, ?> recognizer,
		Object offendingSymbol,
		int line,
		int charPosition,
		String message,
		RecognitionException e)
	{
		this.errors.push(new Error(new Coordinate(line, charPosition), message + "."));
	}
}
