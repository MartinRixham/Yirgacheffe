package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.JavaLanguageType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Map;

public class TypeListener extends ClassListener
{
	public TypeListener(
		String sourceFile,
		Map<String, Type> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(sourceFile, declaredTypes, classLoader, errorListener, writer);
	}

	@Override
	public void enterImportStatement(YirgacheffeParser.ImportStatementContext context)
	{
		String identifier = context.fullyQualifiedType().Identifier().getText();
		Type type = new ReferenceType(context.fullyQualifiedType());

		this.types.putImportedType(identifier, type);
	}

	@Override
	public void enterSimpleType(YirgacheffeParser.SimpleTypeContext context)
	{
		if (context.Identifier() != null)
		{
			if (this.types.containsKey(context.getText()))
			{
				return;
			}

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			try
			{
				classLoader.loadClass("java.lang." + context.getText());

				Type type = new JavaLanguageType(context.getText());

				this.types.putImportedType(context.getText(), type);
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
		if (this.types.containsKey(context.getText()))
		{
			return;
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		try
		{
			classLoader.loadClass(context.getText());

			this.types.putImportedType(context.getText(), new ReferenceType(context));
		}
		catch (ClassNotFoundException e)
		{
			try
			{
				this.classLoader.loadClass(context.getText());

				this.types.putImportedType(context.getText(), new ReferenceType(context));
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
