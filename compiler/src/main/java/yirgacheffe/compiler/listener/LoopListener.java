package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.statement.Block;
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
		if (this.initialiser != null &&
			this.exitCondition != null &&
			this.incrementer != null &&
			this.statements.length() > 0)
		{
			Coordinate coordinate = new Coordinate(context);
			Statement statement = this.statements.pop();

			For forStatement =
				new For(
					this.initialiser,
					this.exitCondition,
					this.incrementer,
					statement);

			this.statements.push(new Block(coordinate, new Array<>(forStatement)));
		}
	}

	public void exitInitialiser(YirgacheffeParser.InitialiserContext context)
	{
		if (context.statementLine() == null)
		{
			String message = "Missing loop initialiser.";

			this.errors.push(new Error(context, message));
		}
		else
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
		if (context.statementLine() == null)
		{
			String message = "Missing loop incrementer.";

			this.errors.push(new Error(context, message));
		}
		else
		{
			this.incrementer = this.statements.pop();
		}
	}
}
