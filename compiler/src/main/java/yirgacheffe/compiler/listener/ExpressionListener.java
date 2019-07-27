package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Bool;
import yirgacheffe.compiler.expression.Char;
import yirgacheffe.compiler.expression.Num;
import yirgacheffe.compiler.expression.Streeng;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.Try;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
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
		try
		{
			Type thisType = this.classes.loadClass(this.className.replace("/", "."));

			this.expressions.push(new This(thisType));
		}
		catch (ClassNotFoundException | NoClassDefFoundError e)
		{
			this.expressions.push(new This(new NullType()));
		}
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

		if (context.StringLiteral() != null)
		{
			this.expressions.push(new Streeng(text));
		}
		else if (context.CharacterLiteral() != null)
		{
			this.expressions.push(new Char(text));
		}
		else if (context.BooleanLiteral() != null)
		{
			this.expressions.push(new Bool(text));
		}
		else
		{
			this.expressions.push(new Num(text));
		}
	}
}
