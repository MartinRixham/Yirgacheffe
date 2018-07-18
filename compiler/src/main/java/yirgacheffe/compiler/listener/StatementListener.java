package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.Return;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.expression.Variable;
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
	public void exitVariableDeclaration(
		YirgacheffeParser.VariableDeclarationContext context)
	{
		Type type = this.types.getType(context.type());

		this.currentVariable = new Variable(this.currentBlock.size() + 1, type);

		this.currentBlock.declare(context.Identifier().getText(), this.currentVariable);
	}

	@Override
	public void enterVariableWrite(YirgacheffeParser.VariableWriteContext context)
	{
		if (this.currentBlock.isDeclared(context.getText()))
		{
			this.currentVariable = this.currentBlock.getVariable(context.getText());
		}
		else
		{
			String message =
				"Assignment to uninitialised variable '" + context.getText() + "'.";

			this.errors.push(new Error(context, message));
		}
	}

	@Override
	public void exitVariableAssignment(
		YirgacheffeParser.VariableAssignmentContext context)
	{
		Expression expression = this.expressions.pop();
		Type type = expression.getType();

		int index = 0;

		if (this.currentVariable != null)
		{
			index = this.currentVariable.getIndex();
		}

		if (this.currentVariable != null &&
			!type.isAssignableTo(this.currentVariable.getType()))
		{
			String message =
				"Cannot assign expression of type " + type +
				" to variable of type " + this.currentVariable.getType() + ".";

			this.errors.push(new Error(context, message));
		}

		this.statements.push(new VariableWrite(index, expression));
	}

	@Override
	public void enterBlock(YirgacheffeParser.BlockContext context)
	{
		this.currentBlock = new Block(this.currentBlock);
	}

	@Override
	public void exitBlock(YirgacheffeParser.BlockContext context)
	{
		this.currentBlock = this.currentBlock.unwarap();
	}

	@Override
	public void exitReturnStatement(YirgacheffeParser.ReturnStatementContext context)
	{
		Expression expression = this.expressions.pop();
		Type type = expression.getType();

		if (!type.isAssignableTo(this.returnType))
		{
			String message =
				"Mismatched return type: Cannot return expression of type " +
				type + " from method of return type " +
				this.returnType + ".";

			this.errors.push(new Error(context, message));
		}

		this.hasReturnStatement = true;

		this.statements.push(new Return(expression));
	}
}
