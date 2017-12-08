package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.parser.YirgacheffeBaseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class YirgacheffeListener extends YirgacheffeBaseListener
{
	private ParseErrorListener errorListener;

	protected ClassWriter writer;

	protected List<Error> errors = new ArrayList<>();

	protected String directory;

	protected String className;

	protected Map<String, Type> importedTypes;

	public YirgacheffeListener(
		String directory,
		Map<String, Type> importedTypes,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		this.directory = directory;
		this.errorListener = errorListener;
		this.writer = writer;
		this.importedTypes = importedTypes;
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
			String classFileName = this.directory + this.className + ".class";

			return new CompilationResult(classFileName, this.writer.toByteArray());
		}
	}
}
