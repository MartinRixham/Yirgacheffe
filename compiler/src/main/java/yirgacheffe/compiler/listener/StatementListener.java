package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.Branch;
import yirgacheffe.compiler.statement.ConditionalStatement;
import yirgacheffe.compiler.statement.Else;
import yirgacheffe.compiler.statement.If;
import yirgacheffe.compiler.statement.OpenBlock;
import yirgacheffe.compiler.statement.Return;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.statement.VariableDeclaration;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class StatementListener extends FieldListener
{
	public StatementListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitVariableDeclaration(
		YirgacheffeParser.VariableDeclarationContext context)
	{
		Type type = this.types.getType(context.type());
		String name = context.Identifier().getText();

		this.statements.push(new VariableDeclaration(name, type));
	}

	@Override
	public void exitVariableAssignment(
		YirgacheffeParser.VariableAssignmentContext context)
	{
		Expression expression = this.expressions.pop();

		String name;

		if (context.variableWrite() == null)
		{
			name = context.variableDeclaration().Identifier().getText();
		}
		else
		{
			name = context.variableWrite().Identifier().getText();
		}

		Coordinate coordinate = new Coordinate(context.getStart());

		this.statements.push(new VariableWrite(name, expression, coordinate));
	}

	@Override
	public void enterBlock(YirgacheffeParser.BlockContext context)
	{
		this.statements.push(new OpenBlock());
	}

	@Override
	public void exitBlock(YirgacheffeParser.BlockContext context)
	{
		Array<Statement> blockStatements = new Array<>();

		while (true)
		{
			Statement statement = this.statements.pop();

			if (statement instanceof OpenBlock)
			{
				break;
			}
			else
			{
				blockStatements.unshift(statement);
			}
		}

		this.statements.push(new Block(blockStatements));
	}

	@Override
	public void exitReturnStatement(YirgacheffeParser.ReturnStatementContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Expression expression = this.expressions.pop();

		this.hasReturnStatement = true;

		this.statements.push(new Return(coordinate, this.returnType, expression));
	}

	@Override
	public void exitIfStatement(YirgacheffeParser.IfStatementContext context)
	{
		Expression condition = this.expressions.pop();
		Statement statement = this.statements.pop();

		If ifStatement = new If(condition, statement);

		this.statements.push(ifStatement);
	}

	@Override
	public void exitElseStatement(YirgacheffeParser.ElseStatementContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Statement statement = this.statements.pop();
		Statement precondition = this.statements.pop();

		Else elseStatement = new Else(coordinate, precondition, statement);

		this.statements.push(elseStatement);
	}

	public void exitConditionalStatement(
		YirgacheffeParser.ConditionalStatementContext context)
	{
		ConditionalStatement conditional = (ConditionalStatement) this.statements.pop();
		Branch branch = new Branch(conditional);

		this.statements.push(branch);
	}
}
