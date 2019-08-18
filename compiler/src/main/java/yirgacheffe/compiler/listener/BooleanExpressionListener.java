package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.comparison.Comparator;
import yirgacheffe.compiler.comparison.Equals;
import yirgacheffe.compiler.comparison.GreaterThan;
import yirgacheffe.compiler.comparison.GreaterThanOrEqual;
import yirgacheffe.compiler.comparison.LessThan;
import yirgacheffe.compiler.comparison.LessThanOrEqual;
import yirgacheffe.compiler.comparison.NotEquals;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Equation;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.parser.YirgacheffeParser;

public class BooleanExpressionListener extends NumericExpressionListener
{
	public BooleanExpressionListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitEquals(YirgacheffeParser.EqualsContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		for (int i = 0; i < context.inequality().size() - 1; i++)
		{
			Expression secondOperand = this.expressions.pop();
			Expression firstOperand = this.expressions.pop();

			Comparator comparator;

			if (context.equative(i).Equal() != null)
			{
				comparator = new Equals();
			}
			else
			{
				comparator = new NotEquals();
			}

			this.expressions.push(
				new Equation(coordinate, comparator, firstOperand, secondOperand));
		}
	}

	@Override
	public void exitInequality(YirgacheffeParser.InequalityContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		for (int i = 0; i < context.add().size() - 1; i++)
		{
			Expression secondOperand = this.expressions.pop();
			Expression firstOperand = this.expressions.pop();

			Comparator comparator;

			if (context.comparative(i).LessThan() != null)
			{
				comparator = new LessThan();
			}
			else if (context.comparative(i).GreaterThan() != null)
			{
				comparator = new GreaterThan();
			}
			else if (context.comparative(i).LessThanOrEqual() != null)
			{
				comparator = new LessThanOrEqual();
			}
			else
			{
				comparator = new GreaterThanOrEqual();
			}

			this.expressions.push(
				new Equation(coordinate, comparator, firstOperand, secondOperand));
		}
	}
}
