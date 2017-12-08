package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
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

	protected String className;

	public YirgacheffeListener(
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		this.errorListener = errorListener;
		this.writer = writer;
	}

	@Override
	public void enterType(YirgacheffeParser.TypeContext context)
	{
		if (context.Identifier() != null)
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			try
			{
				classLoader.loadClass("java.lang." + context.getText());
			}
			catch (ClassNotFoundException e)
			{
				String message =
					"Unrecognised type: " + context.getText() + " is not a type.";

				this.errors.add(new Error(context, message));
			}
		}
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
			String classFileName;

			if (this.packageName == null)
			{
				classFileName = this.className + ".class";
			}
			else
			{
				classFileName =
					this.packageName.replace('.', '/') + "/" +
					this.className + ".class";
			}

			return new CompilationResult(classFileName, this.writer.toByteArray());
		}
	}
}
