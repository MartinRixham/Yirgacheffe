package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.parser.YirgacheffeBaseListener;

import java.util.ArrayList;
import java.util.List;

public class YirgacheffeListener extends YirgacheffeBaseListener
{
	private ParseErrorListener errorListener;

	protected ClassWriter writer;

	protected List<Error> errors = new ArrayList<>();

	public YirgacheffeListener(
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		this.errorListener = errorListener;
		this.writer = writer;
	}

	public CompilationResult getCompilationResult()
	{
		if (this.errorListener.hasError())
		{
			return new CompilationResult(this.errorListener.getErrors());
		}
		else if (this.errors.size() > 0)
		{
			return new CompilationResult(this.errors);
		}
		else
		{
			return new CompilationResult(this.writer.toByteArray());
		}
	}
}
