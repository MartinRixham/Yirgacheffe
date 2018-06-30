package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.function.MatchResult;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeConstructor;
import yirgacheffe.compiler.expression.InvokeMethod;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Functions;
import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FunctionCallListener extends ExpressionListener
{
	private ArgumentClasses argumentClasses;

	private Expression[] arguments;

	public FunctionCallListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitConstructor(YirgacheffeParser.ConstructorContext context)
	{
		if (context.type().primaryType().simpleType() != null &&
			context.type().primaryType().simpleType().PrimitiveType() != null)
		{
			String message =
				"Cannot instantiate primitive type " + context.type().getText() + ".";

			this.errors.add(new Error(context.type(), message));
		}
	}

	@Override
	public void exitInstantiation(YirgacheffeParser.InstantiationContext context)
	{
		Type owner = this.types.getType(context.constructor().type());
		Constructor<?>[] constructors = owner.reflectionClass().getConstructors();
		List<Callable> functions = new ArrayList<>();

		for (Constructor<?> constructor : constructors)
		{
			functions.add(new Function(owner, constructor));
		}

		MatchResult matchResult =
			new Functions(functions).getMatchingExecutable(this.argumentClasses);
		Callable function = matchResult.getFunction();

		Expression invoke = new InvokeConstructor(function, this.arguments);

		this.expressions.add(invoke);

		if (matchResult.isSuccessful())
		{
			for (MismatchedTypes types: matchResult.getMismatchedParameters())
			{
				String message =
					"Argument of type " + types.from() +
					" cannot be assigned to generic parameter of type " +
					types.to() + ".";

				this.errors.add(new Error(context, message));
			}
		}
		else if (!owner.reflectionClass().isPrimitive())
		{
			String constructor = owner.toFullyQualifiedType();

			String message =
				"Constructor " + constructor + this.argumentClasses + " not found.";

			this.errors.add(new Error(context.getStart(), message));
		}
	}

	@Override
	public void exitMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		String methodName = context.Identifier().getText();
		Expression expression = this.expressions.pop();
		Type owner = expression.getType();
		MatchResult matchResult = this.getMatchResult(owner, methodName);
		Callable function = matchResult.getFunction();

		if (matchResult.isSuccessful())
		{
			for (MismatchedTypes types: matchResult.getMismatchedParameters())
			{
				String message =
					"Argument of type " + types.from() +
					" cannot be assigned to generic parameter of type " +
					types.to() + ".";

				this.errors.add(new Error(context, message));
			}
		}
		else if (matchResult.isAmbiguous())
		{
			String method = owner + "." + methodName;
			String message = "Ambiguous call to method " + method + ".";

			this.errors.add(new Error(context.Identifier().getSymbol(), message));
		}
		else
		{
			String method = owner + "." + methodName;
			String message = "Method " + method + this.argumentClasses + " not found.";

			this.errors.add(new Error(context.Identifier().getSymbol(), message));
		}

		this.expressions.add(new InvokeMethod(function, expression, this.arguments));
	}

	private MatchResult getMatchResult(Type owner, String methodName)
	{
		Method[] methods;

		if (owner.toFullyQualifiedType().equals(this.className))
		{
			methods = owner.reflectionClass().getDeclaredMethods();
		}
		else
		{
			methods = owner.reflectionClass().getMethods();
		}

		ArrayList<Callable> namedMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals(methodName))
			{
				namedMethods.add(new Function(owner, method));
			}
		}

		return new Functions(namedMethods)
			.getMatchingExecutable(this.argumentClasses);
	}

	@Override
	public void exitArguments(YirgacheffeParser.ArgumentsContext context)
	{
		int argumentCount = context.expression().size();
		Type[] arguments = new Type[argumentCount];
		this.arguments = new Expression[argumentCount];

		for (int i = context.expression().size() - 1; i >= 0; i--)
		{
			this.arguments[i] = this.expressions.pop();
			arguments[i] = this.arguments[i].getType();
		}

		this.argumentClasses = new ArgumentClasses(arguments);
	}

	@Override
	public void exitFunctionCall(YirgacheffeParser.FunctionCallContext context)
	{
		for (Expression expression: this.expressions)
		{
			expression.compile(this.methodVisitor);
		}

		if (this.expressions.peek().getType() != PrimitiveType.VOID)
		{
			this.methodVisitor.visitInsn(Opcodes.POP);
		}
	}
}
