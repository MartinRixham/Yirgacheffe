package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.DeclaredType;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.Type.ImportedType;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Map;

public class TypeListener extends ClassListener
{
	public TypeListener(
		String directory,
		Map<String, DeclaredType> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, declaredTypes, classLoader, errorListener, writer);
	}

	@Override
	public void enterImportStatement(YirgacheffeParser.ImportStatementContext context)
	{
		String identifier = context.fullyQualifiedType().Identifier().getText();
		ImportedType type = new ImportedType(context.fullyQualifiedType());

		this.importedTypes.put(identifier, type);
	}

	@Override
	public void enterSimpleType(YirgacheffeParser.SimpleTypeContext context)
	{
		if (context.Identifier() != null)
		{
			if (this.importedTypes.containsKey(context.getText()) ||
				this.declaredTypes.containsKey(context.getText()))
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
			try
			{
				this.classLoader.loadClass(context.getText());
			}
			catch (ClassNotFoundException ex)
			{
				String message =
					"Unrecognised type: " + context.getText() + " is not a type.";

				this.errors.add(new Error(context, message));
			}
		}
	}
}
