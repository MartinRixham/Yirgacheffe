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

public class FunctionCallListener extends ExpressionListener
{
	private String argumentDescriptor;

	private Class<?>[] argumentClasses;

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
	}

	@Override
	public void exitInstantiation(YirgacheffeParser.InstantiationContext context)
	{
		Type type = this.types.getType(context.constructor().type());

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			type.toFullyQualifiedType().replace(".", "/"),
			"<init>",
			this.argumentDescriptor + "V",
			false);

		this.typeStack.push(type);
	}

	@Override
	public void exitMethod(YirgacheffeParser.MethodContext context)
	{
		String methodName = context.Identifier().getText();
		Type owner = this.typeStack.pop();

		String descriptor = "()V";
		Type returnType = new NullType();

		try
		{
			Method method =
				owner.reflectionClass().getMethod(methodName, this.argumentClasses);

			Class<?> returnClass = method.getReturnType();

			if (returnClass.isPrimitive())
			{
				returnType = PrimitiveType.valueOf(returnClass.getName().toUpperCase());
			}
			else
			{
				returnType = new ReferenceType(returnClass);
			}

			descriptor = this.argumentDescriptor + returnType.toJVMType();
		}
		catch (NoSuchMethodException e)
		{
			String message =
				"Method " + e.getMessage() + " not found.";

			this.errors.add(
				new Error(context.Identifier().getSymbol(), message));
		}

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			owner.toFullyQualifiedType().replace(".", "/"),
			methodName,
			descriptor,
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