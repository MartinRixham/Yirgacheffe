package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Types;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeBaseListener;
import yirgacheffe.parser.YirgacheffeParser;

public class YirgacheffeListener extends YirgacheffeBaseListener
{
	private String sourceFile;

	protected ClassWriter writer =
		new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

	protected Array<Error> errors = new Array<>();

	protected String packageName;

	protected String directory;

	protected String className = "null";

	protected Types types;

	protected Classes classes;

	public YirgacheffeListener(String sourceFile, Classes classes)
	{
		this.sourceFile = sourceFile;
		this.directory = this.getDirectory(sourceFile);
		this.types = new Types();
		this.classes = classes;
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

	public void exportDefinedTypes()
	{
		byte[] bytes = this.writer.toByteArray();

		String fullyQualifiedType =
			this.packageName == null ?
				this.className :
				this.packageName + "." + this.className;

		this.classes.addClass(fullyQualifiedType, bytes);
	}

	public CompilationResult getCompilationResult()
	{
		if (this.errors.length() > 0)
		{
			return new CompilationResult(this.sourceFile, this.errors);
		}
		else
		{
			String classFileName = this.directory + this.className + ".class";
			byte[] bytes = this.writer.toByteArray();

			return new CompilationResult(classFileName, bytes);
		}
	}

	@Override
	public void enterSemicolon(YirgacheffeParser.SemicolonContext context)
	{
		if (context.SEMI_COLON() == null)
		{
			this.errors.push(new Error(context, "Missing ';'."));
		}
	}
}
