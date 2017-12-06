package yirgacheffe.compiler;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

public class ParseErrorListener extends BaseErrorListener
{
	private List<Error> errors = new ArrayList<>();

	@Override
	public void syntaxError(
		Recognizer<?, ?> recognizer,
		Object offendingSymbol,
		int line,
		int charPosition,
		String message,
		RecognitionException e)
	{
		this.errors.add(new Error(line, charPosition, message));
	}

	public boolean hasError()
	{
		return this.errors.size() > 0;
	}

	public List<Error> getErrors()
	{
		return this.errors;
	}
}
