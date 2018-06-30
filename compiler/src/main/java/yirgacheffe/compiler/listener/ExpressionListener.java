package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Literal;
import yirgacheffe.compiler.expression.This;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
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
		if (this.localVariables.containsKey(context.getText()))
		{
			Variable variable = this.localVariables.get(context.getText());
			Type type = variable.getType();

			this.expressions.add(new VariableRead(variable));

			this.typeStack.push(type);
		}
		else
		{
			String message = "Unknown local variable '" + context.getText() + "'.";

			this.errors.add(new Error(context, message));

			this.typeStack.push(new NullType());
		}
	}

	@Override
	public void enterThisRead(YirgacheffeParser.ThisReadContext context)
	{
		try
		{
			String fullyQualifiedType =
				this.packageName == null ?
					this.className :
					this.packageName + "." + this.className;

			Type thisType = this.classes.loadClass(fullyQualifiedType);

			this.typeStack.push(thisType);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}

		this.expressions.add(new This());
	}

	@Override
	public void enterLiteral(YirgacheffeParser.LiteralContext context)
	{
		Type type;

		if (context.StringLiteral() != null)
		{
			type = new ReferenceType(String.class);

			this.typeStack.push(new ReferenceType(String.class));
		}
		else if (context.CharacterLiteral() != null)
		{
			type = PrimitiveType.CHAR;

			this.typeStack.push(PrimitiveType.CHAR);
		}
		else if (context.BooleanLiteral() != null)
		{
			type = PrimitiveType.BOOLEAN;

			this.typeStack.push(PrimitiveType.BOOLEAN);
		}
		else
		{
			type = PrimitiveType.DOUBLE;

			this.typeStack.push(PrimitiveType.DOUBLE);
		}

		this.expressions.add(new Literal(type, context.getText()));
	}
}
