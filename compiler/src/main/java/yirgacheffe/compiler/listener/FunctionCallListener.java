package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.ArrayType;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Executables;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class FunctionCallListener extends ExpressionListener
{
	private ArgumentClasses argumentClasses;

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
				"Constructor " + constructor + this.argumentClasses + " not found.";

			this.errors.add(new Error(context.getStart(), message));
		}
		else
		{
			this.argumentClasses
				.checkTypeParameter(matchedConstructor, this.constructorType, context);
		}
	}

	@Override
	public void enterMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		Type owner = this.typeStack.pop();

		if (owner instanceof PrimitiveType)
		{
			String typeWithSlashes =
				owner.toFullyQualifiedType().replace(".", "/");

			this.methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				typeWithSlashes,
				"valueOf",
				"(" + owner.toJVMType() + ")L" + typeWithSlashes + ";",
				false);
		}

		this.typeStack.push(owner);
	}

	@Override
	public void exitMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		String methodName = context.Identifier().getText();
		Type owner = this.typeStack.pop();
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
			String method = owner + "." + methodName + "(";
			String message = "Method " + method + this.argumentClasses + " not found.";

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

			this.argumentClasses.checkTypeParameter(matchedMethod, owner, context);
		}

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			owner.toFullyQualifiedType().replace(".", "/"),
			methodName,
			argumentDescriptor + returnType.toJVMType(),
			false);

		if (returnType.equals(PrimitiveType.INT))
		{
			this.methodVisitor.visitInsn(Opcodes.I2D);
			this.typeStack.push(PrimitiveType.DOUBLE);
		}
		else if (!returnType.equals(PrimitiveType.VOID))
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

		this.argumentClasses = new ArgumentClasses(arguments, this.errors);
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
