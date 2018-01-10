package yirgacheffe.compiler.error;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.List;

public class ParseErrorListener extends BaseErrorListener
{
	private List<Error> errors;

	public ParseErrorListener(List<Error> errors)
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
		this.errors.add(new Error(line, charPosition, message + "."));
	}
}
