package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.parser.YirgacheffeParser;

public class ConstructorListener extends MainMethodListener
{
	public ConstructorListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitConstructorDeclaration(
		YirgacheffeParser.ConstructorDeclarationContext context)
	{
		if (!context.Identifier().getText().equals(this.className))
		{
			String message =
				"Constructor of incorrect type " + context.Identifier().getText() +
					": expected " + this.className + ".";

			this.errors.add(new Error(context.Identifier().getSymbol(), message));
		}

		boolean isPrivate = false;

		if (context.Modifier() == null)
		{
			String message =
				"Expected public or private access modifier " +
					"at start of constructor declaration.";

			this.errors.add(new Error(context, message));
		}
		else
		{
			isPrivate = context.Modifier().getText().equals("private");
		}

		this.returnType = PrimitiveType.VOID;

		String descriptor =
			this.getDescriptor(context.parameter()) + "V";

		this.methodVisitor =
			this.writer.visitMethod(
				isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				"<init>",
				descriptor,
				null,
				null);

		this.methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/Object",
			"<init>",
			"()V",
			false);

		this.hasDefaultConstructor = false;
		this.inConstructor = true;
	}
}
