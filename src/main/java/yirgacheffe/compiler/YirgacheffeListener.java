package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.parser.YirgacheffeBaseListener;
import yirgacheffe.parser.YirgacheffeParser;

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

	@Override
	public void enterSimpleType(YirgacheffeParser.SimpleTypeContext context)
	{
		if (context.Identifier() != null)
		{
			if (this.importedTypes.keySet().contains(context.Identifier().getText()))
			{
				return;
			}

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

	@Override
	public void enterFullyQualifiedType(
		YirgacheffeParser.FullyQualifiedTypeContext context)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		try
		{
			classLoader.loadClass(context.getText());
		}
		catch (ClassNotFoundException e)
		{
			String message =
				"Unrecognised type: " + context.getText() + " is not a type.";

			this.errors.add(new Error(context, message));
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
			String classFileName = this.directory + this.className + ".class";

			return new CompilationResult(classFileName, this.writer.toByteArray());
		}
	}
}
