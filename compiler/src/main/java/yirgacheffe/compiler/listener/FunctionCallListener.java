package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.ArrayType;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
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

		Executable matchedConstructor =
			this.getExecutable(Arrays.asList(constructors), argumentDescriptor);

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
			List<String> arguments = new ArrayList<>();

			for (Class<?> argumentClass : this.argumentClasses)
			{
				arguments.add(argumentClass.getName());
			}

			constructor += String.join(",", arguments) + ")";

			String message = "Constructor " + constructor + " not found.";

			this.errors.add(new Error(context.getStart(), message));
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

		Method matchedMethod = this.getExecutable(namedMethods, argumentDescriptor);

		if (matchedMethod == null)
		{
			String method = owner.toFullyQualifiedType() + "." + methodName + "(";
			List<String> arguments = new ArrayList<>();

			for (Class<?> argumentClass: this.argumentClasses)
			{
				arguments.add(argumentClass.getName());
			}

			method += String.join(",", arguments) + ")";

			String message = "Method " + method + " not found.";

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

	private <T extends Executable> T getExecutable(
		List<T> executables,
		StringBuilder argumentDescriptor)
	{
		for (T executable: executables)
		{
			Class<?>[] parameterTypes = executable.getParameterTypes();
			boolean matched = true;

			if (parameterTypes.length != this.argumentClasses.length)
			{
				continue;
			}

			for (int i = 0; i < parameterTypes.length; i++)
			{
				if (!parameterTypes[i].isAssignableFrom(this.argumentClasses[i]) &&
					!parameterTypes[i].getSimpleName().equals(
						this.argumentClasses[i].getSimpleName().toLowerCase()))
				{
					matched = false;
					break;
				}
			}

			if (matched)
			{
				argumentDescriptor.append("(");

				for (Class<?> parameterType: parameterTypes)
				{
					if (parameterType.isPrimitive())
					{
						argumentDescriptor.append(
							PrimitiveType.valueOf(parameterType.getName().toUpperCase())
								.toJVMType());
					}
					else if (parameterType.isArray())
					{
						argumentDescriptor.append(
							new ArrayType(parameterType.getName()).toJVMType());
					}
					else
					{
						argumentDescriptor.append(
							new ReferenceType(parameterType).toJVMType());
					}
				}

				argumentDescriptor.append(")");

				return executable;
			}
		}

		return null;
	}

	@Override
	public void exitArguments(YirgacheffeParser.ArgumentsContext context)
	{
		int argumentCount = context.expression().size();
		Type[] argumentTypes = new Type[argumentCount];
		this.argumentClasses = new Class<?>[argumentCount];

		for (int i = context.expression().size() - 1; i >= 0; i--)
		{
			argumentTypes[i] = this.typeStack.pop();
			this.argumentClasses[i] = argumentTypes[i].reflectionClass();
		}
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
