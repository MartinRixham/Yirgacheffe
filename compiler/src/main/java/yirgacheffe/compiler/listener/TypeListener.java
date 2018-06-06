package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.TypeVariable;

public class TypeListener extends ClassListener
{
	public TypeListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterImportStatement(YirgacheffeParser.ImportStatementContext context)
	{
		String className =
			context.packageName().getText() + "." + context.Identifier().getText();

		try
		{
			Type type = this.classes.loadClass(className);
			String identifier = context.Identifier().getText();

			this.types.put(identifier, type);
		}
		catch (ClassNotFoundException e)
		{
			String message =
				"Unrecognised type: " + className + " is not a type.";

			this.errors.add(new Error(context.packageName(), message));
		}
	}

	@Override
	public void exitType(YirgacheffeParser.TypeContext context)
	{
		Type type = this.types.getType(context);
		TypeVariable[] typeParameters = type.reflectionClass().getTypeParameters();

		if (context.typeParameters() == null)
		{
			if (typeParameters.length > 0)
			{
				String message =
					"Missing type parameters for type " +
					type.toFullyQualifiedType() + ".";

				this.errors.add(new Error(context, message));
			}
		}
		else if (typeParameters.length != context.typeParameters().type().size())
		{
			String message =
				"Type " + type.toFullyQualifiedType() + " requires " +
				typeParameters.length + " parameter(s) but found " +
				context.typeParameters().type().size() + ".";

			this.errors.add(new Error(context, message));
		}
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
				if (this.packageName == null)
				{
					type = this.classes.loadClass(context.getText());
				}
				else
				{
					type =
						this.classes.loadClass(
							this.packageName + "." + context.getText());
				}
			}
			catch (ClassNotFoundException e)
			{
				try
				{
					type =
						this.classes.loadClass("yirgacheffe.lang." + context.getText());
				}
				catch (ClassNotFoundException ex)
				{
					try
					{
						type = this.classes.loadClass("java.lang." + context.getText());
					}
					catch (ClassNotFoundException exc)
					{
						String message =
							"Unrecognised type: " + context.getText() + " is not a type.";

						this.errors.add(new Error(context, message));
					}
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
			Type type = this.classes.loadClass(context.getText());

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
