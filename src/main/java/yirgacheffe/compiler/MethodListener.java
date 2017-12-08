package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.List;
import java.util.Map;

public class MethodListener extends ClassListener
{
	public MethodListener(
		String directory,
		Map<String, Type> importedTypes,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, importedTypes, errorListener, writer);
	}

	@Override
	public void enterInterfaceMethodDeclaration(
		YirgacheffeParser.InterfaceMethodDeclarationContext context)
	{
		YirgacheffeParser.MethodDeclarationContext methodContext =
			context.methodDeclaration();

		if (methodContext.modifier() == null)
		{
			String descriptor =
				this.getMethodDescriptor(methodContext.parameter(), methodContext.type());

			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
				methodContext.Identifier().getText(),
				descriptor,
				null,
				null);
		}
		else
		{
			Error error =
				new Error(
					context,
					"Access modifier is not required for interface method declaration.");

			this.errors.add(error);
		}
	}

	@Override
	public void enterClassMethodDeclaration(
		YirgacheffeParser.ClassMethodDeclarationContext context)
	{
		YirgacheffeParser.MethodDeclarationContext methodContext =
			context.methodDeclaration();

		YirgacheffeParser.ModifierContext modifier = methodContext.modifier();

		if (modifier == null)
		{
			String message =
				"Expected public or private access modifier " +
				"at start of method declaration.";

			this.errors.add(new Error(context, message));
		}
		else
		{
			String descriptor =
				this.getMethodDescriptor(methodContext.parameter(), methodContext.type());

			this.writer.visitMethod(
				modifier.Public() == null ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				methodContext.Identifier().getText(),
				descriptor,
				null,
				null);
		}
	}

	private String getMethodDescriptor(
		List<YirgacheffeParser.ParameterContext> parameters,
		YirgacheffeParser.TypeContext returnType)
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (YirgacheffeParser.ParameterContext parameter : parameters)
		{
			YirgacheffeParser.TypeContext typeContext = parameter.type();

			if (typeContext != null)
			{
				Type type = this.getType(typeContext);

				descriptor.append(type.toJVMType());
			}
		}

		descriptor.append(")");
		descriptor.append(this.getType(returnType).toJVMType());

		return descriptor.toString();
	}

	private Type getType(YirgacheffeParser.TypeContext context)
	{
		String typeName = context.getText();
		Type type;

		if (context.simpleType() != null &&
			this.importedTypes.containsKey(typeName))
		{
			type = this.importedTypes.get(typeName);
		}
		else
		{
			type = new ImportedType(context);
		}

		return type;
	}

	@Override
	public void enterParameter(YirgacheffeParser.ParameterContext context)
	{
		if (context.type() == null)
		{
			Error error =
				new Error(context, "Expected type before argument identifier.");

			this.errors.add(error);
		}
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
}
