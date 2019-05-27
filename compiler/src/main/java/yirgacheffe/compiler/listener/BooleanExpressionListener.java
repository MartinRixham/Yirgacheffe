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
import yirgacheffe.lang.Array;
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
		Array<Expression> expressions = new Array<>();

		for (YirgacheffeParser.InequalityContext c: context.inequality())
		{
			expressions.push(this.expressions.pop());
		}

		for (int i = 0; i < context.inequality().size() - 1; i++)
		{
			Expression firstOperand = expressions.pop();
			Expression secondOperand = expressions.pop();

			Comparator comparator;

			if (context.equative(i).Equal() != null)
			{
				comparator = new Equals();
			}
			else
			{
				comparator = new NotEquals();
			}

			expressions.push(
				new Equation(coordinate, comparator, firstOperand, secondOperand));
		}

		this.expressions.push(expressions.pop());
	}

	@Override
	public void exitInequality(YirgacheffeParser.InequalityContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Array<Expression> expressions = new Array<>();

		for (YirgacheffeParser.AddContext c: context.add())
		{
			expressions.push(this.expressions.pop());
		}

		for (int i = 0; i < context.add().size() - 1; i++)
		{
			Expression firstOperand = expressions.pop();
			Expression secondOperand = expressions.pop();

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

			expressions.push(
				new Equation(coordinate, comparator, firstOperand, secondOperand));
		}

		this.expressions.push(expressions.pop());
	}
}
