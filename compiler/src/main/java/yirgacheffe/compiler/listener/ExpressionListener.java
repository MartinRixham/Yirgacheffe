package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.GetEnumeration;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.Try;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

public class ExpressionListener extends LoopListener
{
	public ExpressionListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterVariableRead(YirgacheffeParser.VariableReadContext context)
	{
		String name = context.getText();

		this.expressions.push(new VariableRead(new Coordinate(context), name));
	}

	@Override
	public void enterThisRead(YirgacheffeParser.ThisReadContext context)
	{
		if (this.inInterface)
		{
			String message = "Cannot reference 'this' in interface constructor.";

			this.errors.push(new Error(context, message));
		}

		Coordinate coordinate = new Coordinate(context);

		this.expressions.push(new This(coordinate, this.thisType));
	}

	@Override
	public void exitAttemptedExpression(
		YirgacheffeParser.AttemptedExpressionContext context)
	{
		if (context.Try() != null)
		{
			this.expressions.push(new Try(this.expressions.pop()));
		}
	}

	@Override
	public void enterLiteral(YirgacheffeParser.LiteralContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		String text = context.getText();

		this.expressions.push(Literal.parse(coordinate, text));
	}

	@Override
	public void exitEnumerationAccess(YirgacheffeParser.EnumerationAccessContext context)
	{
		Coordinate coordinate = new Coordinate(context);
		Type type = this.types.getType(context.type());
		Expression expression = this.expressions.pop();

		this.expressions.push(new GetEnumeration(coordinate, type, expression));
	}
}
