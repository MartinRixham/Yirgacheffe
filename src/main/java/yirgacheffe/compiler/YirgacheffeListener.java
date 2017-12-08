package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.parser.YirgacheffeBaseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class YirgacheffeListener extends YirgacheffeBaseListener
{
	private ParseErrorListener errorListener;

	protected ClassWriter writer;

	protected List<Error> errors = new ArrayList<>();

	protected String directory;

	protected String className;

	protected Map<String, ImportedType> importedTypes = new HashMap<>();

	protected Map<String, DeclaredType> declaredTypes;

	public YirgacheffeListener(
		String directory,
		Map<String, DeclaredType> declaredTypes,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		this.directory = directory;
		this.declaredTypes = declaredTypes;
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
			String classFileName = this.directory + this.className + ".class";

			return new CompilationResult(classFileName, this.writer.toByteArray());
		}
	}
}
