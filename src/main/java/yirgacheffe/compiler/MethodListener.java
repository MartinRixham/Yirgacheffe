package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.parser.YirgacheffeParser;

public class MethodListener extends FieldListener
{
	public MethodListener(ParseErrorListener errorListener, ClassWriter writer)
	{
		super(errorListener, writer);
	}

	@Override
	public void enterMethodDeclaration(
		YirgacheffeParser.MethodDeclarationContext context)
	{
		if (context.Modifier() == null)
		{
			MethodDescriptor descriptor =
				new MethodDescriptor(context.parameter(), context.Type());

			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
				context.Identifier().getText(),
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
	public void enterParameter(YirgacheffeParser.ParameterContext context)
	{
		if (context.Type() == null)
		{
			Error error =
				new Error(context, "Expected type before argument identifier");

			this.errors.add(error);
		}
	}
}
