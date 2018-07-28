package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

public class ExpressionListener extends StatementListener
{
	public ExpressionListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterVariableRead(YirgacheffeParser.VariableReadContext context)
	{
		String name = context.getText();

		this.expressions.push(new VariableRead(name, new Coordinate(context)));
	}

	@Override
	public void enterThisRead(YirgacheffeParser.ThisReadContext context)
	{
		Type thisType;

		try
		{
			String fullyQualifiedType =
				this.packageName == null ?
					this.className :
					this.packageName + "." + this.className;

			thisType = this.classes.loadClass(fullyQualifiedType);

			this.expressions.push(new This(thisType));
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void enterLiteral(YirgacheffeParser.LiteralContext context)
	{
		Type type;

		if (context.StringLiteral() != null)
		{
			type = new ReferenceType(String.class);
		}
		else if (context.CharacterLiteral() != null)
		{
			type = PrimitiveType.CHAR;
		}
		else if (context.BooleanLiteral() != null)
		{
			type = PrimitiveType.BOOLEAN;
		}
		else
		{
			type = PrimitiveType.DOUBLE;
		}

		this.expressions.push(new Literal(type, context.getText()));
	}
}
