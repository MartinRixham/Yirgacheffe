package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.MatchResult;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeConstructor;
import yirgacheffe.compiler.expression.InvokeMethod;
import yirgacheffe.compiler.expression.New;
import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Executables;
import yirgacheffe.compiler.type.Function;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.NullType;
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

		Type type = this.types.getType(context.type());
		String typeWithSlashes = type.toFullyQualifiedType().replace(".", "/");

		this.expressions.add(new New(typeWithSlashes));

		this.typeStack.beginInstantiation();
		this.typeStack.push(type);
	}

	@Override
	public void exitInstantiation(YirgacheffeParser.InstantiationContext context)
	{
		Type owner = this.typeStack.peak();
		Constructor<?>[] constructors = owner.reflectionClass().getConstructors();
		List<Function> functions = new ArrayList<>();

		for (Constructor<?> constructor: constructors)
		{
			functions.add(new Function(owner, constructor));
		}

		MatchResult matchResult =
			new Executables(functions)
				.getMatchingExecutable(this.argumentClasses);

		Expression invoke =
			new InvokeConstructor(
				owner.toFullyQualifiedType().replace(".", "/"),
				matchResult.getDescriptor() + "V");

		this.expressions.add(invoke);
		this.typeStack.endInstantiation();

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
		Type owner = this.typeStack.pop();

		String methodName = context.Identifier().getText();
		Method[] methods;

		if (owner.toFullyQualifiedType().equals(this.className))
		{
			methods = owner.reflectionClass().getDeclaredMethods();
		}
		else
		{
			methods = owner.reflectionClass().getMethods();
		}

		Type returnType = new NullType();
		ArrayList<Function> namedMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals(methodName))
			{
				namedMethods.add(new Function(owner, method));
			}
		}

		MatchResult matchResult =
			new Executables(namedMethods).getMatchingExecutable(this.argumentClasses);

		if (matchResult.isSuccessful())
		{
			returnType = matchResult.getExecutable().getReturnType();

			for (MismatchedTypes types: matchResult.getMismatchedParameters())
			{
				String message =
					"Argument of type " + types.from() +
						" cannot be assigned to generic parameter of type " +
						types.to() + ".";

				this.errors.add(new Error(context, message));
			}
		}
		else
		{
			String method = owner + "." + methodName;
			String message = "Method " + method + this.argumentClasses + " not found.";

			this.errors.add(
				new Error(context.Identifier().getSymbol(), message));
		}

		Expression invoke =
			new InvokeMethod(
				owner,
				methodName,
				matchResult.getDescriptor() + returnType.toJVMType(),
				returnType);

		this.expressions.add(invoke);

		if (!returnType.equals(PrimitiveType.VOID))
		{
			this.typeStack.push(returnType);
		}
	}

	@Override
	public void exitArguments(YirgacheffeParser.ArgumentsContext context)
	{
		int argumentCount = context.expression().size();
		Type[] arguments = new Type[argumentCount];

		for (int i =  0; i < context.expression().size(); i++)
		{
			arguments[i] = this.typeStack.pop();
		}

		Type owner = this.typeStack.peak();

		this.argumentClasses = new ArgumentClasses(arguments, owner);
	}

	@Override
	public void exitFunctionCall(YirgacheffeParser.FunctionCallContext context)
	{
		for (Expression expression: this.expressions)
		{
			expression.compile(this.methodVisitor);
		}

		if (!this.typeStack.isEmpty())
		{
			this.methodVisitor.visitInsn(Opcodes.POP);
		}
	}
}
