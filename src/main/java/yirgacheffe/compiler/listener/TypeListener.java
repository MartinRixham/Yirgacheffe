package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.JavaLanguageType;
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
		String identifier = context.fullyQualifiedType().Identifier().getText();
		Type type = new ReferenceType(context.fullyQualifiedType());

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

			try
			{
				this.classes.loadClass(this.packageName + "." + context.getText());

				Type type = new ReferenceType(this.packageName, context.getText());

				this.types.put(context.getText(), type);
			}
			catch (ClassNotFoundException e)
			{
				try
				{
					this.classes.loadClass("java.lang." + context.getText());

					Type type = new JavaLanguageType(context.getText());

					this.types.put(context.getText(), type);
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
			this.classes.loadClass(context.getText());

			this.types.put(context.getText(), new ReferenceType(context));
		}
		catch (ClassNotFoundException ex)
		{
			String message =
				"Unrecognised type: " + context.getText() + " is not a type.";

			this.errors.add(new Error(context, message));
		}
	}
}
