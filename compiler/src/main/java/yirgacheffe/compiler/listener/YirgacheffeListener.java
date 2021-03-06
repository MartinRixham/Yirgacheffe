package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.parallel.GeneratedClass;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Types;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeBaseListener;
import yirgacheffe.parser.YirgacheffeParser;

public class YirgacheffeListener extends YirgacheffeBaseListener
{
	protected String sourceFile;

	protected ClassNode classNode = new ClassNode();

	protected Array<Error> errors = new Array<>();

	protected String packageName;

	protected String directory;

	protected String className = "null";

	protected Types types;

	protected Classes classes;

	protected Array<GeneratedClass> generatedClasses = new Array<>();

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
		ClassWriter writer =
			new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

		this.classNode.accept(writer);

		byte[] bytes = writer.toByteArray();

		this.classes.addClass(this.className.replace("/", "."), bytes);
	}

	public CompilationResult getCompilationResult()
	{
		if (this.errors.length() > 0)
		{
			return new CompilationResult(this.sourceFile, this.errors);
		}
		else
		{
			ClassWriter writer =
				new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

			this.classNode.visitSource(this.sourceFile, null);
			this.classNode.accept(writer);

			String classFileName = this.className + ".class";
			byte[] bytes = writer.toByteArray();

			return new CompilationResult(classFileName, bytes, this.generatedClasses);
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
