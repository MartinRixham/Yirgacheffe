package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.parser.YirgacheffeParser;

public class MainMethodListener extends MethodListener
{
	public MainMethodListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitMainMethodDeclaration(
		YirgacheffeParser.MainMethodDeclarationContext context)
	{
		this.mainMethodName = context.Identifier().getText();

		String descriptor = this.getDescriptor(context.parameter()) + "V";

		this.methodVisitor =
			this.writer.visitMethod(
				Opcodes.ACC_PUBLIC,
				this.mainMethodName,
				descriptor,
				null,
				null);

		this.returnType = PrimitiveType.VOID;
	}
}
