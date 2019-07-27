package yirgacheffe.compiler.listener;

import org.antlr.v4.runtime.Token;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.UUID;

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
		this.hasConstructor = true;
		this.inConstructor = true;

		YirgacheffeParser.SignatureContext signature = context.signature();
		String className = new Array<>(this.className.split("/")).pop();

		boolean isValid = true;

		if (!signature.Identifier().getText().equals(className))
		{
			String message =
				"Constructor of incorrect type " + signature.Identifier().getText() +
					": expected " + className + ".";

			Token token = signature.Identifier().getSymbol();

			this.errors.push(new Error(token, message));

			isValid = false;
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

		this.methodNode =
			new MethodNode(
				isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
				isValid ? "<init>" : UUID.randomUUID().toString(),
				this.signature.getDescriptor(),
				this.signature.getSignature(),
				null);

		this.classNode.methods.add(this.methodNode);

		if (!isValid)
		{
			return;
		}

		this.methodNode.visitVarInsn(Opcodes.ALOAD, 0);

		this.methodNode.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/Object",
			"<init>",
			"()V",
			false);

		for (String initialiser: this.initialisers)
		{
			this.methodNode.visitVarInsn(Opcodes.ALOAD, 0);

			this.methodNode.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				this.className,
				"0" + initialiser + "_init_field",
				"()V",
				false);
		}

		if (signature.parameter().size() == 0)
		{
			this.hasDefaultConstructor = true;
		}
	}
}
