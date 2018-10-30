package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.comparison.Comparison;
import yirgacheffe.compiler.comparison.Equals;
import yirgacheffe.compiler.comparison.GreaterThan;
import yirgacheffe.compiler.comparison.GreaterThanOrEqual;
import yirgacheffe.compiler.comparison.LessThan;
import yirgacheffe.compiler.comparison.LessThanOrEqual;
import yirgacheffe.compiler.comparison.NotEquals;
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
		Array<Expression> expressions = new Array<>();

		for (YirgacheffeParser.InequalityContext c: context.inequality())
		{
			expressions.push(this.expressions.pop());
		}

		for (int i = 0; i < context.inequality().size() - 1; i++)
		{
			Expression firstOperand = expressions.pop();
			Expression secondOperand = expressions.pop();

			Comparison comparison;

			if (context.equative(i).Equal() != null)
			{
				comparison = new Equals();
			}
			else
			{
				comparison = new NotEquals();
			}

			expressions.push(new Equation(firstOperand, secondOperand, comparison));
		}

		this.expressions.push(expressions.pop());
	}

	@Override
	public void exitInequality(YirgacheffeParser.InequalityContext context)
	{
		Array<Expression> expressions = new Array<>();

		for (YirgacheffeParser.AddContext c: context.add())
		{
			expressions.push(this.expressions.pop());
		}

		for (int i = 0; i < context.add().size() - 1; i++)
		{
			Expression firstOperand = expressions.pop();
			Expression secondOperand = expressions.pop();

			Comparison comparison;

			if (context.comparative(i).LessThan() != null)
			{
				comparison = new LessThan();
			}
			else if (context.comparative(i).GreaterThan() != null)
			{
				comparison = new GreaterThan();
			}
			else if (context.comparative(i).LessThanOrEqual() != null)
			{
				comparison = new LessThanOrEqual();
			}
			else
			{
				comparison = new GreaterThanOrEqual();
			}

			expressions.push(new Equation(firstOperand, secondOperand, comparison));
		}

		this.expressions.push(expressions.pop());
	}
}
