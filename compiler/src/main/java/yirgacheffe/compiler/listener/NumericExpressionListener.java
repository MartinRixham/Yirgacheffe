package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.BinaryNumericOperation;
import yirgacheffe.compiler.expression.BooleanOperation;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Negation;
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

		for (YirgacheffeParser.UnaryExpressionContext c: context.unaryExpression())
		{
			coordinates.push(new Coordinate(c));
			expressions.push(this.expressions.pop());
		}

		for (int i = 0; i < context.unaryExpression().size() - 1; i++)
		{
			Expression firstOperand = expressions.pop();
			Expression secondOperand = expressions.pop();

			int opcode;
			String description;

			if (context.multiplicative(i).Remainder() != null)
			{
				opcode = Opcodes.DREM;
				description = "find remainder of";
			}
			else if (context.multiplicative(i).Divide() != null)
			{
				opcode = Opcodes.DDIV;
				description = "divide";
			}
			else
			{
				opcode = Opcodes.DMUL;
				description = "multiply";
			}

			expressions.push(new BinaryNumericOperation(
				coordinates.pop(),
				opcode,
				description,
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

			int opcode;
			String description;

			if (context.additive(i).Subtract() != null)
			{
				opcode = Opcodes.DSUB;
				description = "subtract";
			}
			else
			{
				opcode = Opcodes.DADD;
				description = "add";
			}

			expressions.push(new BinaryNumericOperation(
				coordinates.pop(),
				opcode,
				description,
				firstOperand,
				secondOperand
			));
		}

		this.expressions.push(expressions.pop());
	}

	@Override
	public void exitNegation(YirgacheffeParser.NegationContext context)
	{
		Coordinate coordinate = new Coordinate(context);

		this.expressions.push(new Negation(coordinate, this.expressions.pop()));
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
					Opcodes.IFNE,
					Opcodes.IFNONNULL,
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
					Opcodes.IFEQ,
					Opcodes.IFNULL,
					firstOperand,
					secondOperand);

			expressions.push(or);
		}

		this.expressions.push(expressions.pop());
	}
}
