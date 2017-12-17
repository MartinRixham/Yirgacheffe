package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Parameters;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Method;

public class FunctionCallListener extends ExpressionListener
{
	public FunctionCallListener(
		String sourceFile,
		Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterInstantiation(YirgacheffeParser.InstantiationContext context)
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
	}

	@Override
	public void exitInstantiation(YirgacheffeParser.InstantiationContext context)
	{
		int argumentCount = context.expression().size();
		Type[] argumentTypes = new Type[argumentCount];

		for (int i = context.expression().size() - 1; i >= 0; i--)
		{
			argumentTypes[i] = this.typeStack.pop();
		}

		String descriptor = new Parameters(argumentTypes).getDescriptor();

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/String",
			"<init>",
			descriptor + "V",
			false);

		this.methodVisitor.visitInsn(Opcodes.POP);

		Type type = this.types.getType(context.type());

		this.typeStack.push(type);
	}

	@Override
	public void exitMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		int argumentCount = context.expression().size() - 1;
		Type[] argumentTypes = new Type[argumentCount];
		Class<?>[] argumentClasses = new Class<?>[argumentCount];

		for (int i = context.expression().size() - 2; i >= 0; i--)
		{
			argumentTypes[i] = this.typeStack.pop();
			argumentClasses[i] = argumentTypes[i].reflectionClass();
		}

		String methodName = context.Identifier().getText();
		Type owner = this.typeStack.pop();
		boolean hasMethod = false;

		for (Method method: owner.reflectionClass().getMethods())
		{
			if (method.getName().equals(methodName))
			{
				hasMethod = true;
				break;
			}
		}

		Parameters parameters = new Parameters(argumentTypes);
		String descriptor = "()V";

		if (hasMethod)
		{
			try
			{
				Method method =
					owner.reflectionClass().getMethod(methodName, argumentClasses);
				Type returnType = new ReferenceType(method.getReturnType());

				descriptor = parameters.getDescriptor() + returnType.toJVMType();
			}
			catch (NoSuchMethodException ex)
			{
				String message =
					"No overload of method '" + methodName +
						"' with parameters " + parameters + ".";

				this.errors.add(new Error(context.Identifier().getSymbol(), message));
			}
		}
		else
		{
			String message =
				"No method '" + methodName +
				"' on object of type " + owner + ".";

			this.errors.add(new Error(context.Identifier().getSymbol(), message));
		}

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			owner.toFullyQualifiedType().replace(".", "/"),
			methodName,
			descriptor,
			false);

		this.methodVisitor.visitInsn(Opcodes.POP);
	}
}
