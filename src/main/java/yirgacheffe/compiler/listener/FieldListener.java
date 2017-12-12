package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.Type.Type;
import yirgacheffe.parser.YirgacheffeParser;

public class FieldListener extends ConstructorListener
{
	public FieldListener(
		String sourceFile,
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(sourceFile, types, classLoader, errorListener, writer);
	}

	@Override
	public void enterFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		if (context.type() == null)
		{
			Error error =
				new Error(context, "Field declaration should start with type.");

			this.errors.add(error);
		}

		String identifier = context.Identifier().getText();
		Type type = this.types.getType(context.type());

		this.writer
			.visitField(
				Opcodes.ACC_PRIVATE,
				identifier,
				type.toJVMType(),
				null,
				null);
	}

	@Override
	public void enterFieldInitialisation(
		YirgacheffeParser.FieldInitialisationContext context)
	{
		this.methodVisitor =
			this.writer.visitMethod(
				Opcodes.ACC_PRIVATE,
				"<init_field_0>",
				"()V",
				null,
				null);

		this.methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
	}

	@Override
	public void exitFieldInitialisation(
		YirgacheffeParser.FieldInitialisationContext context)
	{
		YirgacheffeParser.FieldDeclarationContext declaration =
			context.fieldDeclaration();

		this.methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			declaration.Identifier().getText(),
			this.types.getType(declaration.type()).toJVMType());

		this.methodVisitor.visitInsn(Opcodes.RETURN);

		Type type = this.types.getType(declaration.type());

		if (!this.checkTypes(type, context.expression()))
		{
			this.errors.add(new Error(context, ""));
		}
	}

	private boolean checkTypes(
		Type fieldType,
		YirgacheffeParser.ExpressionContext expression)
	{
		YirgacheffeParser.LiteralContext literal = expression.literal();
		String jvmType = fieldType.toJVMType();

		if (literal.BooleanLiteral() != null)
		{
			return jvmType.equals("B");
		}
		else
		{
			return true;
		}
	}
}
