package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeConstructor;
import yirgacheffe.compiler.expression.InvokeMethod;
import yirgacheffe.compiler.expression.InvokeThis;
import yirgacheffe.compiler.statement.FunctionCall;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class FunctionCallListener extends ExpressionListener
{
	public FunctionCallListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitInstantiation(YirgacheffeParser.InstantiationContext context)
	{
		if (context.type().primaryType().simpleType() != null &&
			context.type().primaryType().simpleType().PrimitiveType() != null)
		{
			String message =
				"Cannot instantiate primitive type " + context.type().getText() + ".";

			this.errors.push(new Error(context.type(), message));
		}

		Type owner = this.types.getType(context.type());
		Coordinate coordinate = new Coordinate(context);

		Expression invoke =
			new InvokeConstructor(
				coordinate,
				owner,
				this.arguments);

		this.expressions.push(invoke);
	}

	@Override
	public void exitSelfInstantiation(YirgacheffeParser.SelfInstantiationContext context)
	{
		if (!this.inConstructor)
		{
			String message = "Cannot call this() outside of constructor.";

			this.errors.push(new Error(context, message));
		}

		try
		{
			String fullyQualifiedType = this.className.replace("/", ".");

			Type thisType = this.classes.loadClass(fullyQualifiedType);

			Coordinate coordinate = new Coordinate(context);

			Expression invoke =
				new InvokeThis(
					coordinate,
					thisType,
					this.arguments);

			this.expressions.push(invoke);
		}
		catch (ClassNotFoundException | NoClassDefFoundError e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void exitDelegation(YirgacheffeParser.DelegationContext context)
	{
		this.hasDelegate = true;

		Coordinate coordinate = new Coordinate(context);

		this.expressions.push(new Delegate(coordinate, this.className, this.arguments));

		if (!this.inConstructor)
		{
			String message = "Cannot set delegate outside of constructor.";

			this.errors.push(new Error(context, message));
		}
	}

	@Override
	public void exitMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		String methodName = context.Identifier().getText();
		Expression expression = this.expressions.pop();
		Coordinate coordinate = new Coordinate(context);

		InvokeMethod invokeMethod =
			new InvokeMethod(
				coordinate,
				methodName,
				this.className,
				expression,
				this.arguments);

		this.expressions.push(invokeMethod);
	}

	@Override
	public void exitArguments(YirgacheffeParser.ArgumentsContext context)
	{
		Array<Expression> arguments = new Array<>();

		for (int i = context.expression().size() - 1; i >= 0; i--)
		{
			arguments.unshift(this.expressions.pop());
		}

		this.arguments = arguments;
	}

	@Override
	public void exitFunctionCall(YirgacheffeParser.FunctionCallContext context)
	{
		Expression expression = this.expressions.pop();

		this.statements.push(new FunctionCall(expression));
	}
}
