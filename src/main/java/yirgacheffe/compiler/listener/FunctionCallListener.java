package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Method;
import java.util.Map;

public class FunctionCallListener extends ExpressionListener
{
	public FunctionCallListener(
		String sourceFile,
		Map<String, Type> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(sourceFile, declaredTypes, classLoader, errorListener, writer);
	}

	@Override
	public void exitInstantiation(YirgacheffeParser.InstantiationContext context)
	{
		if (context.type().simpleType() != null &&
			context.type().simpleType().PrimitiveType() != null)
		{
			String message =
				"Cannot instantiate primitive type " + context.type().getText() + ".";

			this.errors.add(new Error(context.type(), message));
		}

		this.methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/String");
		this.methodVisitor.visitInsn(Opcodes.DUP);

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/String",
			"<init>",
			"()V",
			false);

		this.methodVisitor.visitInsn(Opcodes.POP);

		Type type = this.types.getType(context.type());

		this.typeStack.push(type);
	}

	@Override
	public void exitMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		Class<?>[] argumentClasses = new Class<?>[context.expression().size() - 1];

		for (int i = context.expression().size() - 2; i >= 0; i--)
		{
			Type type = this.typeStack.pop();

			try
			{
				argumentClasses[i] =
					this.classLoader.loadClass(type.toFullyQualifiedType());
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}
		}

		String methodName = context.Identifier().getText();

		Class<?> owner;

		try
		{
			owner =
				this.classLoader.loadClass(this.typeStack.pop().toFullyQualifiedType());
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException();
		}

		boolean hasMethod = false;

		for (Method method: owner.getMethods())
		{
			if (method.getName().equals(methodName))
			{
				hasMethod = true;
			}
		}

		if (hasMethod)
		{
			try
			{
				owner.getMethod(methodName, argumentClasses);
			}
			catch (NoSuchMethodException ex)
			{
				String message =
					"No overload of method '" + methodName + "' with parameters (num).";

				this.errors.add(new Error(context.Identifier().getSymbol(), message));
			}
		}
		else
		{
			String message =
				"No method '" + methodName + "' on object of type " +
					owner.getName() + ".";

			this.errors.add(new Error(context.Identifier().getSymbol(), message));
		}

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			"java/lang/String",
			methodName,
			"()Ljava/lang/String;",
			false);

		this.methodVisitor.visitInsn(Opcodes.POP);
	}
}
