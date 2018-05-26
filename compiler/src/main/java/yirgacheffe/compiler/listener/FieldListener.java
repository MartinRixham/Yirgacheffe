package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.HashMap;
import java.util.Map;

public class FieldListener extends ConstructorListener
{
	private Map<String, Type> fieldTypes = new HashMap<>();

	public FieldListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		if (context.type() == null)
		{
			Error error =
				new Error(context, "Field declaration should start with type.");

			this.errors.add(error);
		}

		String fieldName = context.Identifier().getText();
		Type type = this.types.getType(context.type());

		this.writer
			.visitField(
				Opcodes.ACC_PRIVATE,
				fieldName,
				type.toJVMType(),
				null,
				null);

		this.fieldTypes.put(fieldName, type);
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
	public void enterFieldWrite(YirgacheffeParser.FieldWriteContext context)
	{
		String message = "Fields must be assigned in initialisers or constructors.";

		this.errors.add(new Error(context, message));
	}
}
