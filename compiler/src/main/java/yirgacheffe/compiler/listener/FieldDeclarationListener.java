package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.ConstantType;
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
			Coordinate coordinate = new Coordinate(context);
			String field = declarationContext.Identifier().getSymbol().getText();

			this.methodNode =
				new MethodNode(
					Opcodes.ACC_PRIVATE,
					"0init_field_" + field,
					"()V",
					null,
					null);

			this.classNode.methods.add(this.methodNode);

			this.expressions.push(new This(coordinate, this.thisType));
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

		if (context.type() == null)
		{
			String message = "Field declaration should start with type.";

			this.errors.push(new Error(context, message));
		}

		if (this.fields.contains(identifier))
		{
			String message = "Duplicate field '" + identifier + "'.";

			this.errors.push(new Error(context, message));

			return;
		}
		else
		{
			this.fields.add(identifier);
		}

		if (context.Const() == null)
		{
			String fieldName = context.Identifier().getText();
			Type type = this.types.getType(context.type());

			this.classNode.fields.add(
				new FieldNode(
					Opcodes.ACC_PROTECTED,
					fieldName,
					type.toJVMType(),
					type.getSignature(),
					null));
		}
	}

	@Override
	public void enterConstantConstructor(
		YirgacheffeParser.ConstantConstructorContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Literal literal = Literal.parse(coordinate, context.literal().getText());
		ConstantType constantType = new ConstantType(this.thisType);

		if (!constantType.matches(literal.getType()))
		{
			String message =
				"Enumeration constant " + literal.getValue() +
				" is not of type " + constantType + ".";

			this.errors.push(new Error(context, message));
		}

		this.methodNode =
			new MethodNode(
				Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
				literal.getValue().toString(),
				"()" + this.thisType.toJVMType(),
				null,
				null);

		this.classNode.methods.add(this.methodNode);
	}

	@Override
	public void exitConstantConstructor(
		YirgacheffeParser.ConstantConstructorContext context)
	{
		this.methodNode.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
		this.methodNode.instructions.add(new InsnNode(Opcodes.ARETURN));
	}
}
