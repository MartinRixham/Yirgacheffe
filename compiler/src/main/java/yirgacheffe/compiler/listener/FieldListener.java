package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

public class FieldListener extends FieldDeclarationListener
{
	public FieldListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterFieldInitialisation(
		YirgacheffeParser.FieldInitialisationContext context)
	{
		this.methodVisitor =
			this.writer.visitMethod(
				Opcodes.ACC_PRIVATE,
				"0_init_field",
				"()V",
				null,
				null);

		this.enterThisRead(null);

		this.hasInitialiser = true;
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

		Type fieldType = this.types.getType(declaration.type());
		Type expressionType = this.typeStack.pop();
		this.typeStack.pop();

		this.methodVisitor.visitMaxs(this.typeStack.reset(), 1);

		if (!fieldType.toJVMType().equals(expressionType.toJVMType()))
		{
			String message =
				"Cannot assign " + expressionType.toString() +
				" to field of type " + fieldType.toString() + ".";

			this.errors.add(new Error(context, message));
		}
	}

	@Override
	public void enterFieldRead(YirgacheffeParser.FieldReadContext context)
	{
		String fieldName = context.Identifier().getText();
		Type fieldType = this.fieldTypes.get(fieldName);

		if (fieldType == null)
		{
			fieldType = new NullType();

			String message = "Unknown field '" + fieldName + "'.";

			this.errors.add(new Error(context.Identifier().getSymbol(), message));
		}

		Type ownerType = this.typeStack.pop();

		this.typeStack.push(fieldType);

		this.methodVisitor.visitFieldInsn(
			Opcodes.GETFIELD,
			ownerType.toFullyQualifiedType(),
			fieldName,
			fieldType.toJVMType());
	}

	@Override
	public void exitFieldWrite(YirgacheffeParser.FieldWriteContext context)
	{
		if (!this.inConstructor || context.expression().get(0).thisRead() == null)
		{
			String message = "Fields must be assigned in initialisers or constructors.";

			this.errors.add(new Error(context, message));
		}

		String fieldName = context.Identifier().getText();
		Type expressionType = this.typeStack.pop();
		Type ownerType = this.typeStack.pop();

		try
		{
			Class<?> fieldClass =
				ownerType.reflectionClass().getDeclaredField(fieldName).getType();
			Class<?> expressionClass = expressionType.reflectionClass();

			if (!fieldClass.isAssignableFrom(expressionClass) &&
				!fieldClass.getSimpleName()
					.equals(expressionClass.getSimpleName().toLowerCase()))
			{
				String message =
					"Cannot assign expression of type " + expressionType +
					" to field of type " + fieldClass.getName() + ".";

				this.errors.add(new Error(context, message));
			}
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}

		this.methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			ownerType.toFullyQualifiedType(),
			fieldName,
			expressionType.toJVMType());
	}
}
