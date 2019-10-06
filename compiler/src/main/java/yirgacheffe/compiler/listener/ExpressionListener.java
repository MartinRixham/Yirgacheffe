package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Enumeration;
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
		this.expressions.push(new This(this.thisType));
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
		String text = context.getText();

		this.expressions.push(Literal.parse(text));
	}

	@Override
	public void exitEnumeration(YirgacheffeParser.EnumerationContext context)
	{
		Type type = this.types.getType(context.type());
		Expression expression = this.expressions.pop();

		this.expressions.push(new Enumeration(type, expression));
	}
}
