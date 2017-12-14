package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Types;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeBaseListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YirgacheffeListener extends YirgacheffeBaseListener
{
	private ParseErrorListener errorListener;

	private String sourceFile;

	protected ClassWriter writer;

	protected List<Error> errors = new ArrayList<>();

	protected String packageName;

	protected String directory;

	protected String className = "null";

	protected Types types;

	protected BytecodeClassLoader classLoader;

	public YirgacheffeListener(
		String sourceFile,
		Map<String, Type> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		this.sourceFile = sourceFile;
		this.directory = this.getDirectory(sourceFile);
		this.types = new Types(declaredTypes);
		this.classLoader = classLoader;
		this.errorListener = errorListener;
		this.writer = writer;
	}

	private String getDirectory(String filePath)
	{
		String[] files = filePath.split("/");
		StringBuilder directory = new StringBuilder();

		for (int i = 0; i < files.length - 1; i++)
		{
			directory.append(files[i]).append("/");
		}

		return directory.toString();
	}

	public CompilationResult getCompilationResult()
	{
		if (this.errorListener.hasError())
		{
			return new CompilationResult(this.sourceFile, this.errorListener.getErrors());
		}
		else if (this.errors.size() > 0)
		{
			return new CompilationResult(this.sourceFile, this.errors);
		}
		else
		{
			String classFileName = this.directory + this.className + ".class";
			byte[] bytes = this.writer.toByteArray();

			this.classLoader.addClass(this.packageName + "." + this.className, bytes);

			return new CompilationResult(classFileName, bytes);
		}
	}

	@Override
	public void enterSemicolon(YirgacheffeParser.SemicolonContext context)
	{
		if (context.SEMI_COLON() == null)
		{
			this.errors.add(new Error(context, "Missing ';'."));
		}
	}
}
