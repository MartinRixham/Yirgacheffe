package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FunctionCallListener extends ExpressionListener
{
	private String argumentDescriptor;

	private Class<?>[] argumentClasses;

	private Type constructorType;

	public FunctionCallListener(
		String sourceFile,
		Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitConstructor(YirgacheffeParser.ConstructorContext context)
	{
		if (context.type().simpleType() != null &&
			context.type().simpleType().PrimitiveType() != null)
		{
			String message =
				"Cannot instantiate primitive type " + context.type().getText() + ".";

			this.errors.add(new Error(context.type(), message));
		}

		Type type = this.types.getType(context.type());
		String typeWithSlashes = type.toFullyQualifiedType().replace(".", "/");

		this.methodVisitor.visitTypeInsn(Opcodes.NEW, typeWithSlashes);
		this.methodVisitor.visitInsn(Opcodes.DUP);

		this.constructorType = this.types.getType(context.type());

		this.typeStack.beginInstantiation();
		this.typeStack.push(this.constructorType);
	}

	@Override
	public void exitInstantiation(YirgacheffeParser.InstantiationContext context)
	{
		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			this.constructorType.toFullyQualifiedType().replace(".", "/"),
			"<init>",
			this.argumentDescriptor + "V",
			false);

		this.typeStack.endInstantiation();
	}

	@Override
	public void exitMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		String methodName = context.Identifier().getText();
		Type owner = this.typeStack.pop();
		Method[] methods = owner.reflectionClass().getMethods();
		Method matchedMethod = null;
		Type returnType = new NullType();
		String argumentDescriptor = "()";

		for (Method method: methods)
		{
			if (method.getName().equals(methodName))
			{
				Class<?>[] parameterTypes = method.getParameterTypes();
				boolean matched = true;

				if (parameterTypes.length != this.argumentClasses.length)
				{
					continue;
				}

				for (int i = 0; i < parameterTypes.length; i++)
				{
					if (!parameterTypes[i].isAssignableFrom(this.argumentClasses[i]))
					{
						matched = false;
						break;
					}
				}

				if (matched)
				{
					matchedMethod = method;

					argumentDescriptor = "(";

					for (Class<?> parameterType: parameterTypes)
					{
						argumentDescriptor +=
							new ReferenceType(parameterType).toJVMType();
					}

					argumentDescriptor += ")";
				}
			}
		}

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

			if (returnClass.isPrimitive())
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

		this.argumentDescriptor = this.getDescriptor(argumentTypes);
	}

	private String getDescriptor(Type[] argumentTypes)
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (Type type : argumentTypes)
		{
			descriptor.append(type.toJVMType());
		}

		descriptor.append(")");

		return descriptor.toString();
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
