package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.main.CompilationResult;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeBaseListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.ArrayList;
import java.util.List;

public class YirgacheffeListener extends YirgacheffeBaseListener
{
	private ParseErrorListener errorListener;

	protected ClassWriter writer;

	protected List<Error> errors = new ArrayList<>();

	protected String packageName;

	protected String directory;

	protected String className = "null";

	protected Types types;

	protected BytecodeClassLoader classLoader;

	public YirgacheffeListener(
		String directory,
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		this.directory = directory;
		this.types = types;
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

	@Override
	public void enterSemiColon(YirgacheffeParser.SemiColonContext context)
	{
		if (context.SEMI_COLON() == null)
		{
			this.errors.add(new Error(context, "Missing semicolon."));
		}
	}
}
