package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.parser.YirgacheffeParser;

public class MethodListener extends FieldListener
{
	public MethodListener(
		String directory,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, errorListener, writer);
	}

	@Override
	public void enterInterfaceMethodDeclaration(
		YirgacheffeParser.InterfaceMethodDeclarationContext context)
	{
		YirgacheffeParser.MethodDeclarationContext methodContext =
			context.methodDeclaration();

		if (methodContext.modifier() == null)
		{
			MethodDescriptor descriptor =
				new MethodDescriptor(methodContext.parameter(), methodContext.type());

			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
				methodContext.Identifier().getText(),
				descriptor.toString(),
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
			MethodDescriptor descriptor =
				new MethodDescriptor(methodContext.parameter(), methodContext.type());

			this.writer.visitMethod(
				modifier.Public() == null ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				methodContext.Identifier().getText(),
				descriptor.toString(),
				null,
				null);
		}
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
}
