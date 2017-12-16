package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.parser.YirgacheffeParser;

public class TypeListener extends ClassListener
{
	public TypeListener(
		String sourceFile,
		Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterImportStatement(YirgacheffeParser.ImportStatementContext context)
	{
		Type type;

		try
		{
			Class<?> loadedClass =
				this.classes.loadClass(context.fullyQualifiedType().getText());

			type = new ReferenceType(loadedClass);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}

		String identifier = context.fullyQualifiedType().Identifier().getText();

		this.types.put(identifier, type);
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

			Type type = new NullType();

			try
			{
				Class<?> loadedClass =
					this.classes.loadClass(
						this.packageName + "." + context.getText());

				type = new ReferenceType(loadedClass);
			}
			catch (ClassNotFoundException e)
			{
				try
				{
					Class<?> loadedClass =
						this.classes.loadClass("java.lang." + context.getText());

					type = new ReferenceType(loadedClass);
				}
				catch (ClassNotFoundException ex)
				{
					String message =
						"Unrecognised type: " + context.getText() + " is not a type.";

					this.errors.add(new Error(context, message));
				}
			}

			this.types.put(context.getText(), type);
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

		try
		{
			Class<?> loadedClass = this.classes.loadClass(context.getText());

			this.types.put(context.getText(), new ReferenceType(loadedClass));
		}
		catch (ClassNotFoundException ex)
		{
			String message =
				"Unrecognised type: " + context.getText() + " is not a type.";

			this.errors.add(new Error(context, message));
		}
	}
}
