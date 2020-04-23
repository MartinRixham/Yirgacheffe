package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.statement.Block;
import yirgacheffe.compiler.statement.DoNothing;
import yirgacheffe.compiler.statement.For;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class LoopListener extends StatementListener
{
	private Statement initialiser;

	private Expression exitCondition;

	private Statement incrementer;

	public LoopListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	public void exitForStatement(YirgacheffeParser.ForStatementContext context)
	{
		if (this.exitCondition != null &&
			this.statements.length() > 0)
		{
			Coordinate coordinate = new Coordinate(context);

			For forStatement =
				new For(
					this.handleMissing(this.initialiser),
					this.exitCondition,
					this.handleMissing(this.incrementer),
					this.getStatement(context.statement()));

			this.statements.push(new Block(coordinate, new Array<>(forStatement)));
		}
	}

	private Statement getStatement(YirgacheffeParser.StatementContext context)
	{
		if (context == null)
		{
			return new DoNothing();
		}
		else
		{
			return this.statements.pop();
		}
	}

	private Statement handleMissing(Statement statement)
	{
		if (statement == null)
		{
			return new DoNothing();
		}
		else
		{
			return statement;
		}
	}

	public void exitInitialiser(YirgacheffeParser.InitialiserContext context)
	{
		if (context.statementLine() != null)
		{
			this.initialiser = this.statements.pop();
		}
	}

	public void exitExitCondition(YirgacheffeParser.ExitConditionContext context)
	{
		if (context.expression() == null)
		{
			String message = "Missing loop exit condition.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.exitCondition = this.expressions.pop();
		}
	}

	public void exitIncrementer(YirgacheffeParser.IncrementerContext context)
	{
		if (context.statementLine() != null)
		{
			this.incrementer = this.statements.pop();
		}
	}
}
