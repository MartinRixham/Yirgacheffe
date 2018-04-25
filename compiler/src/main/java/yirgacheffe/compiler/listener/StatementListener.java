package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.parser.YirgacheffeParser;

public class StatementListener extends FieldListener
{
	private Variable currentVariable;

	public StatementListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterLiteral(YirgacheffeParser.LiteralContext context)
	{
		Object value;

		if (context.StringLiteral() != null)
		{
			value = context.getText().replace("\"", "");

			this.typeStack.push(new ReferenceType(String.class));
		}
		else if (context.CharacterLiteral() != null)
		{
			value = context.getText().charAt(1);

			this.typeStack.push(PrimitiveType.CHAR);
		}
		else if (context.BooleanLiteral() != null)
		{
			value = context.getText().equals("true");

			this.typeStack.push(PrimitiveType.BOOLEAN);
		}
		else
		{
			value = new Double(context.getText());

			this.typeStack.push(PrimitiveType.DOUBLE);
		}

		this.methodVisitor.visitLdcInsn(value);
	}

	@Override
	public void exitVariableDeclaration(
		YirgacheffeParser.VariableDeclarationContext context)
	{
		Type type = this.types.getType(context.type());

		int index = 1;

		for (Variable variable: this.localVariables.values())
		{
			index += variable.getType().width();
		}

		this.currentVariable = new Variable(index, type);

		this.localVariables.put(context.Identifier().getText(), this.currentVariable);
	}

	@Override
	public void enterVariableWrite(YirgacheffeParser.VariableWriteContext context)
	{
		if (this.localVariables.containsKey(context.getText()))
		{
			this.currentVariable = this.localVariables.get(context.getText());
		}
		else
		{
			String message =
				"Assignment to uninitialised variable '" + context.getText() + "'.";

			this.errors.add(new Error(context, message));
		}
	}

	@Override
	public void exitVariableAssignment(
		YirgacheffeParser.VariableAssignmentContext context)
	{
		Type type = this.typeStack.pop();
		int index = 0;

		if (this.currentVariable != null)
		{
			index = this.currentVariable.getIndex();
		}

		this.methodVisitor.visitVarInsn(type.getStoreInstruction(), index);

		if (
			!(type instanceof NullType) &&
			this.currentVariable != null &&
			!this.currentVariable.getType().reflectionClass()
				.isAssignableFrom(type.reflectionClass()))
		{
			String message =
				"Cannot assign expression of type " + type +
				" to variable of type " + this.currentVariable.getType() + ".";

			this.errors.add(new Error(context, message));
		}
	}
}
