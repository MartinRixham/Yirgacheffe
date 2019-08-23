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
import yirgacheffe.compiler.expression.MultiEquation;
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

		if (context.comparative().size() == 0)
		{
			return;
		}
		if (context.comparative().size() == 1)
		{
			Expression secondOperand = this.expressions.pop();
			Expression firstOperand = this.expressions.pop();

			Comparator comparator = getComparator(context.comparative(0));

			this.expressions.push(
					new Equation(coordinate, comparator, firstOperand, secondOperand));

			return;
		}

		Array<Comparator> comparators = new Array<>();
		Array<Expression> expressions = new Array<>();

		for (int i = 0; i < context.comparative().size(); i++)
		{
			Comparator comparator = getComparator(context.comparative(i));

			comparators.push(comparator);
		}

		for (int i = 0; i < context.add().size(); i++)
		{
			expressions.push(this.expressions.get(i));
		}

		MultiEquation equation =
			new MultiEquation(coordinate, comparators, expressions);

		this.expressions.push(equation);
	}

	private Comparator getComparator(YirgacheffeParser.ComparativeContext context)
	{
		if (context.LessThan() != null)
		{
			return new LessThan();
		}
		else if (context.GreaterThan() != null)
		{
			return new GreaterThan();
		}
		else if (context.LessThanOrEqual() != null)
		{
			return new LessThanOrEqual();
		}
		else
		{
			return new GreaterThanOrEqual();
		}
	}
}
