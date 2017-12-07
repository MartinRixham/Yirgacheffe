package yirgacheffe.compiler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.parser.YirgacheffeParser;

public class ClassListener extends YirgacheffeListener
{
	public ClassListener(ParseErrorListener errorListener, ClassWriter writer)
	{
		super(errorListener, writer);
	}

	@Override
	public void enterMalformedDeclaration(
		YirgacheffeParser.MalformedDeclarationContext context)
	{
		this.errors.add(
			new Error(context, "Expected declaration of class or interface."));
	}

	@Override
	public void enterClassDeclaration(
		YirgacheffeParser.ClassDeclarationContext context)
	{
		if (context.Identifier() == null)
		{
			this.errors.add(new Error(context, "Class identifier expected."));
		}
		else
		{
			String className = context.Identifier().getText();

			this.writer.visit(
				Opcodes.V1_8,
				Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
				className,
				null,
				"java/lang/Object",
				null);

			this.makeConstructor();
		}
	}

	private void makeConstructor()
	{
		this.writer.visitMethod(
			Opcodes.ACC_PUBLIC,
			"<init>",
			"()V",
			null,
			null);
	}

	@Override
	public void enterInterfaceDeclaration(
		YirgacheffeParser.InterfaceDeclarationContext context)
	{
		if (context.Identifier() == null)
		{
			this.errors.add(new Error(context, "Interface identifier expected."));
		}
		else
		{
			String className = context.Identifier().getText();

			this.writer.visit(
				Opcodes.V1_8,
				Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
				className,
				null,
				"java/lang/Object",
				null);
		}
	}
}
