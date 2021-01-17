package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.UnaryOperation;
import yirgacheffe.compiler.statement.AttemptedStatement;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.Branch;
import yirgacheffe.compiler.statement.ConditionalStatement;
import yirgacheffe.compiler.statement.DoNothing;
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
	public void exitStatement(YirgacheffeParser.StatementContext context)
	{
		if (context.SEMI_COLON() != null)
		{
			this.statements.push(new DoNothing());
		}
	}

	@Override
	public void exitStatementLine(YirgacheffeParser.StatementLineContext context)
	{
		if (context.variableDeclaration() != null)
		{
			String message =
				"Variable '" + context.variableDeclaration().Identifier() +
				"' declared without assignment.";

			this.errors.push(new Error(context, message));
		}
	}

	@Override
	public void exitVariableDeclaration(
		YirgacheffeParser.VariableDeclarationContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Type type = this.types.getType(context.type());
		String name = context.Identifier().getText();

		this.statements.push(new VariableDeclaration(coordinate, name, type));
	}

	@Override
	public void exitVariableAssignment(
		YirgacheffeParser.VariableAssignmentContext context)
	{
		Expression expression = this.expressions.pop();
		Coordinate coordinate = new Coordinate(context.getStart());

		if (context.variableDeclaration() == null)
		{
			String name = context.Identifier().getText();

			this.statements.push(new VariableWrite(coordinate, name, expression));
		}
		else
		{
			VariableDeclaration declaration = (VariableDeclaration) this.statements.pop();

			this.statements.push(new VariableWrite(coordinate, declaration, expression));
		}
	}

	@Override
	public void enterBlock(YirgacheffeParser.BlockContext context)
	{
		this.statements.push(new OpenBlock());
	}

	@Override
	public void exitBlock(YirgacheffeParser.BlockContext context)
	{
		Array<Statement> blockStatements = this.getStatements(new Array<>());
		Coordinate coordinate = new Coordinate(context.stop.getLine(), 0);
		Block block = new Block(coordinate, blockStatements);

		this.statements.push(block);
	}

	private Array<Statement> getStatements(Array<Statement> blockStatements)
	{
		Statement statement = this.statements.pop();

		if (statement instanceof OpenBlock)
		{
			return blockStatements;
		}
		else
		{
			blockStatements.unshift(statement);

			return this.getStatements(blockStatements);
		}
	}

	@Override
	public void exitReturnStatement(YirgacheffeParser.ReturnStatementContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		if (context.expression() == null)
		{
			this.statements.push(new Return(coordinate, this.returnType));
		}
		else
		{
			Expression expression = this.expressions.pop();

			this.statements.push(new Return(coordinate, this.returnType, expression));
		}
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

	@Override
	public void exitConditionalStatement(
		YirgacheffeParser.ConditionalStatementContext context)
	{
		Statement statement = this.statements.pop();

		if (statement instanceof ConditionalStatement)
		{
			ConditionalStatement conditional = (ConditionalStatement) statement;
			Branch branch = new Branch(conditional);

			this.statements.push(branch);
		}
		else
		{
			this.statements.push(new DoNothing());
		}
	}

	@Override
	public void exitPostincrementStatement(
		YirgacheffeParser.PostincrementStatementContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Expression expression = this.expressions.pop();

		UnaryOperation postincrement =
			new UnaryOperation(coordinate, expression, false, true);

		this.statements.push(postincrement);
	}

	@Override
	public void exitPreincrementStatement(
		YirgacheffeParser.PreincrementStatementContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Expression expression = this.expressions.pop();

		UnaryOperation preincrement =
			new UnaryOperation(coordinate, expression, true, true);

		this.statements.push(preincrement);
	}

	@Override
	public void exitPostdecrementStatement(
		YirgacheffeParser.PostdecrementStatementContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Expression expression = this.expressions.pop();

		UnaryOperation postdecrement =
			new UnaryOperation(coordinate, expression, false, false);

		this.statements.push(postdecrement);
	}

	@Override
	public void exitPredecrementStatement(
		YirgacheffeParser.PredecrementStatementContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Expression expression = this.expressions.pop();

		UnaryOperation predecrement =
			new UnaryOperation(coordinate, expression, true, false);

		this.statements.push(predecrement);
	}

	public void exitAttemptedStatement(
		YirgacheffeParser.AttemptedStatementContext context)
	{
		this.statements.push(new AttemptedStatement(this.statements.pop()));
	}
}
