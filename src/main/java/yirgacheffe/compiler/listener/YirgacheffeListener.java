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
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		this.sourceFile = sourceFile;
		this.directory = this.getDirectory(sourceFile);
		this.types = types;
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

	@Override
	public void enterCloseBlock(YirgacheffeParser.CloseBlockContext context)
	{
		if (context.CLOSE_BLOCK() == null)
		{
			this.errors.add(new Error(context, "Missing '}'."));
		}
	}

	@Override
	public void enterCloseBracket(YirgacheffeParser.CloseBracketContext context)
	{
		if (context.CLOSE_BRACKET() == null)
		{
			this.errors.add(new Error(context, "Missing ')'."));
		}
	}
}
