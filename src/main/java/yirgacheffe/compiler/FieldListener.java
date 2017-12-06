package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.parser.YirgacheffeParser;

public class FieldListener extends ClassListener
{
	public FieldListener(ParseErrorListener errorListener, ClassWriter writer)
	{
		super(errorListener, writer);
	}

	@Override
	public void enterFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		if (context.Type() == null)
		{
			Error error =
				new Error(context, "Field declaration should start with type.");

			this.errors.add(error);
		}
		else
		{
			Type type = Type.parse(context.Type().getSymbol().getText());
			String identifier = context.Identifier().getSymbol().getText();

			this.writer
				.visitField(
					Opcodes.ACC_PRIVATE,
					identifier,
					type.getJVMType(),
					null,
					null);
		}
	}

	@Override
	public void enterInterfaceFieldDeclaration(
		YirgacheffeParser.InterfaceFieldDeclarationContext context)
	{
		this.errors.add(new Error(context, "Interface cannot contain field."));
	}
}