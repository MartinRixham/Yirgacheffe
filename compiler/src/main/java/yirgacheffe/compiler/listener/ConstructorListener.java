package yirgacheffe.compiler.listener;

import org.antlr.v4.runtime.Token;
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
	public void enterConstructorDeclaration(
		YirgacheffeParser.ConstructorDeclarationContext context)
	{
		this.returnType = PrimitiveType.VOID;
	}

	@Override
	public void exitConstructorDeclaration(
		YirgacheffeParser.ConstructorDeclarationContext context)
	{
		YirgacheffeParser.SignatureContext signature = context.signature();

		if (!signature.Identifier().getText().equals(this.className))
		{
			String message =
				"Constructor of incorrect type " + signature.Identifier().getText() +
					": expected " + this.className + ".";

			Token token = signature.Identifier().getSymbol();

			this.errors.push(new Error(token, message));
		}

		boolean isPrivate = false;

		if (context.modifier() == null)
		{
			String message =
				"Expected public or private access modifier " +
					"at start of constructor declaration.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			isPrivate = context.modifier().Private() != null;
		}

		this.methodVisitor =
			this.writer.visitMethod(
				isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				"<init>",
				this.signature.getDescriptor(),
				this.signature.getSignature(),
				null);

		this.methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/Object",
			"<init>",
			"()V",
			false);

		for (int i = 0; i < this.initialiserCount; i++)
		{
			this.methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			this.methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				this.className,
				i + "_init_field",
				"()V",
				false);
		}

		if (signature.parameter().size() == 0)
		{
			this.hasDefaultConstructor = true;
		}

		this.hasConstructor = true;
		this.inConstructor = true;
	}
}
