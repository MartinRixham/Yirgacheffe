package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.UnaryOperation;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.Branch;
import yirgacheffe.compiler.statement.ConditionalStatement;
import yirgacheffe.compiler.statement.Else;
import yirgacheffe.compiler.statement.ForCondition;
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
	private ForCondition forCondition;

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
		Coordinate coordinate = new Coordinate(context.getStart());

		if (context.variableWrite() == null)
		{
			VariableDeclaration declaration = (VariableDeclaration) this.statements.pop();

			this.statements.push(new VariableWrite(coordinate, declaration, expression));
		}
		else
		{
			String name = context.variableWrite().Identifier().getText();

			this.statements.push(new VariableWrite(coordinate, name, expression));
		}
	}

	@Override
	public void enterBlock(YirgacheffeParser.BlockContext context)
	{
		this.statements.push(new OpenBlock());
		this.forCondition = null;
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

		Coordinate coordinate = new Coordinate(context.stop.getLine(), 0);

		if (this.forCondition != null)
		{
			blockStatements =
				new Array<>(this.forCondition.getStatement(blockStatements));
		}

		this.statements.push(new Block(coordinate, blockStatements));
	}

	@Override
	public void exitReturnStatement(YirgacheffeParser.ReturnStatementContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		if (context.expression() == null)
		{
			this.statements.push(new Return(coordinate));
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

	public void exitConditionalStatement(
		YirgacheffeParser.ConditionalStatementContext context)
	{
		ConditionalStatement conditional = (ConditionalStatement) this.statements.pop();
		Branch branch = new Branch(conditional);

		this.statements.push(branch);
	}

	public void exitForStatement(YirgacheffeParser.ForStatementContext context)
	{
		Statement incrementer = this.statements.pop();
		Expression exitCondition = this.expressions.pop();
		Statement initialiser = this.statements.pop();

		this.forCondition = new ForCondition(initialiser, exitCondition, incrementer);
	}

	public void exitPostincrementStatement(
		YirgacheffeParser.PostincrementStatementContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Expression expression = this.expressions.pop();
		UnaryOperation postincrement = new UnaryOperation(coordinate, expression, false);

		this.statements.push(postincrement);
	}
}
