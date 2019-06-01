package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.BinaryOperation;
import yirgacheffe.compiler.expression.BooleanOperation;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Negation;
import yirgacheffe.compiler.expression.Operator;
import yirgacheffe.compiler.expression.StringConcatenation;
import yirgacheffe.compiler.expression.UnaryOperation;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class NumericExpressionListener extends FunctionCallListener
{
	public NumericExpressionListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitMultiply(YirgacheffeParser.MultiplyContext context)
	{
		Array<Coordinate> coordinates = new Array<>();
		Array<Expression> expressions = new Array<>();

		for (YirgacheffeParser.UnaryOperationContext c: context.unaryOperation())
		{
			coordinates.push(new Coordinate(c));
			expressions.push(this.expressions.pop());
		}

		for (int i = 0; i < context.unaryOperation().size() - 1; i++)
		{
			Expression firstOperand = expressions.pop();
			Expression secondOperand = expressions.pop();

			Operator operator;

			if (context.multiplicative(i).Remainder() != null)
			{
				operator = Operator.REMAINDER;
			}
			else if (context.multiplicative(i).Divide() != null)
			{
				operator = Operator.DIVIDE;
			}
			else
			{
				operator = Operator.MULTIPLY;
			}

			expressions.push(
				new BinaryOperation(
					coordinates.pop(),
					operator,
					firstOperand,
					secondOperand
				));
		}

		this.expressions.push(expressions.pop());
	}

	@Override
	public void exitAdd(YirgacheffeParser.AddContext context)
	{
		Array<Coordinate> coordinates = new Array<>();
		Array<Expression> expressions = new Array<>();

		for (YirgacheffeParser.MultiplyContext c: context.multiply())
		{
			coordinates.push(new Coordinate(c));
			expressions.push(this.expressions.pop());
		}

		for (int i = 0; i < context.multiply().size() - 1; i++)
		{
			Expression firstOperand = expressions.pop();
			Expression secondOperand = expressions.pop();

			Operator operator;

			if (context.additive(i).Subtract() != null)
			{
				operator = Operator.SUBTRACT;
			}
			else
			{
				operator = Operator.ADD;
			}

			expressions.push(
				new BinaryOperation(
					coordinates.pop(),
					operator,
					firstOperand,
					secondOperand));
		}

		Expression expression = expressions.pop();

		if (expression instanceof BinaryOperation)
		{
			this.expressions.push(new StringConcatenation((BinaryOperation) expression));
		}
		else
		{
			this.expressions.push(expression);
		}
	}

	@Override
	public void exitNegation(YirgacheffeParser.NegationContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		this.expressions.push(new Negation(coordinate, this.expressions.pop()));
	}

	@Override
	public void exitPreincrement(YirgacheffeParser.PreincrementContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		this.expressions.push(
			new UnaryOperation(coordinate, this.expressions.pop(), true, true));
	}

	@Override
	public void exitPostincrement(YirgacheffeParser.PostincrementContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		this.expressions.push(
			new UnaryOperation(coordinate, this.expressions.pop(), false, true));
	}

	@Override
	public void exitPredecrement(YirgacheffeParser.PredecrementContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		this.expressions.push(
			new UnaryOperation(coordinate, this.expressions.pop(), true, false));
	}

	@Override
	public void exitPostdecrement(YirgacheffeParser.PostdecrementContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		this.expressions.push(
			new UnaryOperation(coordinate, this.expressions.pop(), false, false));
	}

	@Override
	public void exitAnd(YirgacheffeParser.AndContext context)
	{
		Array<Expression> expressions = new Array<>();

		for (YirgacheffeParser.EqualsContext c: context.equals())
		{
			expressions.push(this.expressions.pop());
		}

		for (int i = 0; i < context.equals().size() - 1; i++)
		{
			Expression firstOperand = expressions.pop();
			Expression secondOperand = expressions.pop();

			BooleanOperation and =
				new BooleanOperation(
					Opcodes.IFEQ,
					Opcodes.IFNULL,
					firstOperand,
					secondOperand);

			expressions.push(and);
		}

		this.expressions.push(expressions.pop());
	}

	@Override
	public void exitOr(YirgacheffeParser.OrContext context)
	{
		Array<Expression> expressions = new Array<>();

		for (YirgacheffeParser.AndContext c: context.and())
		{
			expressions.push(this.expressions.pop());
		}

		for (int i = 0; i < context.and().size() - 1; i++)
		{
			Expression firstOperand = expressions.pop();
			Expression secondOperand = expressions.pop();

			BooleanOperation or =
				new BooleanOperation(
					Opcodes.IFNE,
					Opcodes.IFNONNULL,
					firstOperand,
					secondOperand);

			expressions.push(or);
		}

		this.expressions.push(expressions.pop());
	}
}
