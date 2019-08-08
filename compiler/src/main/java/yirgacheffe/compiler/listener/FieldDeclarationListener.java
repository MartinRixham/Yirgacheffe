package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashSet;
import java.util.Set;

public class FieldDeclarationListener extends MethodListener
{
	private Set<String> fields = new HashSet<>();

	public FieldDeclarationListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterFieldInitialisation(
		YirgacheffeParser.FieldInitialisationContext context)
	{
		YirgacheffeParser.FieldDeclarationContext declarationContext =
			context.fieldDeclaration();

		if (declarationContext.Const() == null)
		{
			String field = declarationContext.Identifier().getSymbol().getText();

			this.methodNode =
				new MethodNode(
					Opcodes.ACC_PRIVATE,
					"0init_field_" + field,
					"()V",
					null,
					null);

			this.classNode.methods.add(this.methodNode);

			this.enterThisRead(null);
		}
	}

	@Override
	public void exitFieldInitialisation(
		YirgacheffeParser.FieldInitialisationContext context)
	{
		if (this.methodNode != null)
		{
			this.methodNode.instructions.add(new InsnNode(Opcodes.RETURN));
		}
	}

	@Override
	public void exitFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		String identifier = context.Identifier().getText();

		if (this.fields.contains(identifier))
		{
			String message = "Duplicate field '" + identifier + "'.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.fields.add(identifier);
		}

		if (context.type() == null)
		{
			String message = "Field declaration should start with type.";

			this.errors.push(new Error(context, message));
		}

		if (context.Const() == null)
		{
			String fieldName = context.Identifier().getText();
			Type type = this.types.getType(context.type());

			int access = Opcodes.ACC_PROTECTED;

			this.classNode.fields.add(
				new FieldNode(
					access,
					fieldName,
					type.toJVMType(),
					type.getSignature(),
					null));
		}
	}
}
