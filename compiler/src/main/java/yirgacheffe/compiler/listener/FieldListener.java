package yirgacheffe.compiler.listener;

import org.antlr.v4.runtime.Token;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.FieldRead;
import yirgacheffe.compiler.statement.FieldWrite;
import yirgacheffe.compiler.type.Variables;
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
				this.initialiserCount + "_init_field",
				"()V",
				null,
				null);

		this.enterThisRead(null);

		this.initialiserCount++;
	}

	@Override
	public void enterFieldDeclaration(YirgacheffeParser.FieldDeclarationContext context)
	{
		if (context.Modifier() != null)
		{
			String message = "Field cannot be declared with access modifier.";

			this.errors.push(new Error(context, message));
		}
	}

	@Override
	public void exitFieldInitialisation(
		YirgacheffeParser.FieldInitialisationContext context)
	{
		YirgacheffeParser.FieldDeclarationContext declaration =
			context.fieldDeclaration();
		Variables variables = new Variables();
		Expression expression = this.expressions.pop();
		Expression self = this.expressions.pop();
		Type expressionType = expression.check(variables);
		Type fieldType = this.types.getType(declaration.type());

		self.compile(this.methodVisitor);
		expression.compile(this.methodVisitor);

		this.methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			declaration.Identifier().getText(),
			this.types.getType(declaration.type()).toJVMType());

		this.methodVisitor.visitInsn(Opcodes.RETURN);
		this.methodVisitor.visitMaxs(0, 0);

		if (!expressionType.isAssignableTo(fieldType))
		{
			String message =
				"Cannot assign " + expressionType.toString() +
				" to field of type " + fieldType.toString() + ".";

			this.errors.push(new Error(context, message));
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
			Token token = context.Identifier().getSymbol();

			this.errors.push(new Error(token, message));
		}

		Expression owner = this.expressions.pop();
		Expression fieldRead = new FieldRead(owner, fieldName, fieldType);

		this.expressions.push(fieldRead);
	}

	@Override
	public void exitFieldWrite(YirgacheffeParser.FieldWriteContext context)
	{
		if (!this.inConstructor || context.expression().get(0).thisRead() == null)
		{
			String message = "Fields must be assigned in initialisers or constructors.";

			this.errors.push(new Error(context, message));
		}

		Coordinate coordinate = new Coordinate(context);
		String fieldName = context.Identifier().getText();
		Expression value = this.expressions.pop();
		Expression owner = this.expressions.pop();

		this.statements.push(new FieldWrite(coordinate, fieldName, owner, value));
	}
}
