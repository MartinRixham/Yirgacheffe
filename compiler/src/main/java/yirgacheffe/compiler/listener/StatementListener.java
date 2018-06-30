package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.expression.Variable;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Stack;

public class StatementListener extends FieldListener
{
	private Variable currentVariable;

	public StatementListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterStatement(YirgacheffeParser.StatementContext context)
	{
		this.expressions = new Stack<>();
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

		for (Expression expression: this.expressions)
		{
			expression.compile(this.methodVisitor);
		}

		this.methodVisitor.visitVarInsn(type.getStoreInstruction(), index);

		if (this.currentVariable != null &&
			!type.isAssignableTo(this.currentVariable.getType()))
		{
			String message =
				"Cannot assign expression of type " + type +
				" to variable of type " + this.currentVariable.getType() + ".";

			this.errors.add(new Error(context, message));
		}
	}

	@Override
	public void exitReturnStatement(YirgacheffeParser.ReturnStatementContext context)
	{
		Type expressionType = this.typeStack.pop();

		if (!expressionType.isAssignableTo(this.returnType))
		{
			String message =
				"Mismatched return type: Cannot return expression of type " +
				expressionType + " from method of return type " +
				this.returnType + ".";

			this.errors.add(new Error(context, message));
		}

		this.hasReturnStatement = true;

		for (Expression expression: this.expressions)
		{
			expression.compile(this.methodVisitor);
		}
	}
}
