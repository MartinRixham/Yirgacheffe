package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.ArrayType;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Executables;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionCallListener extends ExpressionListener
{
	private Class<?>[] argumentClasses;

	private Type constructorType;

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

		this.methodVisitor.visitTypeInsn(Opcodes.NEW, typeWithSlashes);
		this.methodVisitor.visitInsn(Opcodes.DUP);

		this.constructorType = type;

		this.typeStack.beginInstantiation();
		this.typeStack.push(this.constructorType);
	}

	@Override
	public void exitInstantiation(YirgacheffeParser.InstantiationContext context)
	{
		Constructor<?>[] constructors =
			this.constructorType.reflectionClass().getConstructors();

		StringBuilder argumentDescriptor = new StringBuilder();

		Constructor matchedConstructor =
			new Executables<Constructor>(Arrays.asList(constructors))
				.getExecutable(this.argumentClasses, argumentDescriptor);

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			this.constructorType.toFullyQualifiedType().replace(".", "/"),
			"<init>",
			argumentDescriptor + "V",
			false);

		this.typeStack.endInstantiation();

		if (matchedConstructor == null)
		{
			if (this.constructorType.reflectionClass().isPrimitive())
			{
				return;
			}

			String constructor = this.constructorType.toFullyQualifiedType() + "(";

			String message =
				"Constructor " + constructor +
				this.formatArguments(this.argumentClasses) + " not found.";

			this.errors.add(new Error(context.getStart(), message));
		}
		else
		{
			this.checkTypeParameter(matchedConstructor);
		}
	}

	@Override
	public void exitMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		String methodName = context.Identifier().getText();
		Type owner = this.typeStack.pop();
		Method[] methods = owner.reflectionClass().getMethods();
		Type returnType = new NullType();
		StringBuilder argumentDescriptor = new StringBuilder();
		ArrayList<Method> namedMethods = new ArrayList<>();

		for (Method method: methods)
		{
			if (method.getName().equals(methodName))
			{
				namedMethods.add(method);
			}
		}

		Method matchedMethod =
			new Executables<>(namedMethods)
				.getExecutable(this.argumentClasses, argumentDescriptor);

		if (matchedMethod == null)
		{
			String method = owner.toFullyQualifiedType() + "." + methodName + "(";
			String message =
				"Method " + method +
				this.formatArguments(this.argumentClasses) + " not found.";

			this.errors.add(
				new Error(context.Identifier().getSymbol(), message));
		}
		else
		{
			Class<?> returnClass = matchedMethod.getReturnType();

			if (returnClass.isArray())
			{
				returnType = new ArrayType(returnClass.getName());
			}
			else if (returnClass.isPrimitive())
			{
				returnType = PrimitiveType.valueOf(returnClass.getName().toUpperCase());
			}
			else
			{
				returnType = new ReferenceType(returnClass);
			}
		}

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			owner.toFullyQualifiedType().replace(".", "/"),
			methodName,
			argumentDescriptor + returnType.toJVMType(),
			false);

		if (!returnType.equals(PrimitiveType.VOID))
		{
			this.typeStack.push(returnType);
		}
	}

	private String formatArguments(Class<?>[] argumentClasses)
	{
		List<String> arguments = new ArrayList<>();

		for (Class<?> argumentClass : argumentClasses)
		{
			arguments.add(argumentClass.getName());
		}

		return String.join(",", arguments) + ")";
	}

	private void checkTypeParameter(Executable executable)
	{
		java.lang.reflect.Type[] parameters = executable.getGenericParameterTypes();

		for (int i = 0; i < parameters.length; i++)
		{
			if (parameters[i] instanceof TypeVariable &&
				!this.constructorType.hasTypeParameter(this.argumentClasses[i]))
			{
				this.errors.add(new Error(0, 1, ""));
			}
		}
	}

	@Override
	public void exitArguments(YirgacheffeParser.ArgumentsContext context)
	{
		int argumentCount = context.expression().size();
		Class<?>[] arguments = new Class<?>[argumentCount];

		for (int i =  0; i < context.expression().size(); i++)
		{
			arguments[i] = this.typeStack.pop().reflectionClass();
		}

		this.argumentClasses = arguments;
	}

	@Override
	public void exitFunctionCall(YirgacheffeParser.FunctionCallContext context)
	{
		if (!this.typeStack.isEmpty())
		{
			this.methodVisitor.visitInsn(Opcodes.POP);
		}
	}
}
