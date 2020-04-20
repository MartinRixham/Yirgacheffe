package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.TypeVariable;

public class TypeListener extends ImplementationListener
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
		catch (ClassNotFoundException | NoClassDefFoundError e)
		{
			String message =
				"Unrecognised type: " + className + " is not a type.";

			this.errors.push(new Error(context.packageName(), message));
		}
	}

	@Override
	public void exitType(YirgacheffeParser.TypeContext context)
	{
		Type type = this.types.getType(context);
		Array<TypeVariable<?>> typeParameters = type.reflect().getTypeParameters();

		if (context.typeParameters() == null)
		{
			if (typeParameters.length() > 0)
			{
				String message =
					"Missing type parameters for type " + type + ".";

				this.errors.push(new Error(context, message));
			}
		}
		else if (typeParameters.length() != context.typeParameters().type().size())
		{
			String message =
				"Type " + type.toFullyQualifiedType().replace('/', '.') + " requires " +
				typeParameters.length() + " parameter(s) but found " +
				context.typeParameters().type().size() + ".";

			this.errors.push(new Error(context, message));
		}
	}

	@Override
	public void enterSimpleType(YirgacheffeParser.SimpleTypeContext context)
	{
		if (context.Identifier() != null)
		{
			String name = context.getText();

			if (this.types.containsKey(name))
			{
				return;
			}

			Type type = new NullType(name);

			try
			{
				if (this.packageName == null)
				{
					type = this.classes.loadClass(name);
				}
				else
				{
					type =
						this.classes.loadClass(
							this.packageName + "." + name);
				}
			}
			catch (ClassNotFoundException | NoClassDefFoundError e)
			{
				try
				{
					type =
						this.classes.loadClass("yirgacheffe.lang." + name);
				}
				catch (ClassNotFoundException | NoClassDefFoundError ex)
				{
					try
					{
						type = this.classes.loadClass("java.lang." + name);
					}
					catch (ClassNotFoundException | NoClassDefFoundError exc)
					{
						String message =
							"Unrecognised type: " + name + " is not a type.";

						this.errors.push(new Error(context, message));
					}
				}
			}

			this.types.put(name, type);
		}
	}

	@Override
	public void enterFullyQualifiedType(
		YirgacheffeParser.FullyQualifiedTypeContext context)
	{
		String name = context.getText();

		if (this.types.containsKey(name))
		{
			return;
		}

		try
		{
			Type type = this.classes.loadClass(name);

			this.types.put(name, type);
		}
		catch (ClassNotFoundException | NoClassDefFoundError ex)
		{
			String message =
				"Unrecognised type: " + name + " is not a type.";

			this.errors.push(new Error(context, message));
		}
	}
}
