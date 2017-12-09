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

	protected String packageName;

	protected String directory;

	protected String className;

	protected Map<String, ImportedType> importedTypes = new HashMap<>();

	protected Map<String, DeclaredType> declaredTypes;

	protected ByteCodeClassLoader classLoader;

	public YirgacheffeListener(
		String directory,
		Map<String, DeclaredType> declaredTypes,
		ByteCodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		this.directory = directory;
		this.declaredTypes = declaredTypes;
		this.classLoader = classLoader;
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
			byte[] bytes = this.writer.toByteArray();

			this.classLoader.addClass(this.packageName + "." + this.className, bytes);

			return new CompilationResult(classFileName, bytes);
		}
	}
}
