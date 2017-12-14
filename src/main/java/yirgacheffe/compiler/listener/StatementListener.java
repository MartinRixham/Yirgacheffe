package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.JavaLanguageType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class StatementListener extends FieldListener
{
	public StatementListener(
		String sourceFile,
		Map<String, Type> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(sourceFile, declaredTypes, classLoader, errorListener, writer);
	}

	@Override
	public void enterLiteral(YirgacheffeParser.LiteralContext context)
	{
		Object value;

		if (context.StringLiteral() != null)
		{
			value = context.getText().replace("\"", "");

			this.typeStack.push(new JavaLanguageType("String"));
		}
		else if (context.CharacterLiteral() != null)
		{
			value = context.getText().charAt(1);
		}
		else if (context.BooleanLiteral() != null)
		{
			value = context.getText().equals("true");

			this.typeStack.push(new PrimitiveType("bool"));
		}
		else
		{
			value = new Double(context.getText());

			this.typeStack.push(new PrimitiveType("num"));
		}

		this.methodVisitor.visitLdcInsn(value);
	}

	@Override
	public void enterVariableDeclaration(
		YirgacheffeParser.VariableDeclarationContext context)
	{
		Type type = this.types.getType(context.type());

		Variable variable =
			new Variable(this.localVariables.size(), type);

		this.localVariables.put(context.Identifier().getText(), variable);
	}

	@Override
	public void enterVariableRead(YirgacheffeParser.VariableReadContext context)
	{
		if (this.localVariables.containsKey(context.getText()))
		{
			this.typeStack.push(this.localVariables.get(context.getText()).getType());
		}
	}

	@Override
	public void enterVariableWrite(YirgacheffeParser.VariableWriteContext context)
	{
		if (!this.localVariables.containsKey(context.getText()))
		{
			String message =
				"Assignment to uninitialised variable '" + context.getText() + "'.";

			this.errors.add(new Error(context, message));
		}
	}

	@Override
	public void exitVariableAssignment(
		YirgacheffeParser.VariableAssignmentContext context)
	{
		Type type = this.typeStack.pop();
		int variableNumber = this.localVariables.size();

		if (type.equals(new PrimitiveType("num")))
		{
			this.methodVisitor.visitVarInsn(Opcodes.DSTORE, variableNumber);
		}
		else if (type.equals(new PrimitiveType("bool")))
		{
			this.methodVisitor.visitVarInsn(Opcodes.ISTORE, variableNumber);
		}
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
		Queue<Class<?>> loadedClasses = new LinkedList<>();

		for (YirgacheffeParser.ExpressionContext expression: context.expression())
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			Type type = this.typeStack.pop();

			try
			{
				loadedClasses.add(
					this.classLoader.loadClass(type.toFullyQualifiedType()));
			}
			catch (ClassNotFoundException e)
			{
				try
				{
					loadedClasses.add(classLoader.loadClass(type.toFullyQualifiedType()));
				}
				catch (ClassNotFoundException ex)
				{
					throw new RuntimeException(ex);
				}
			}
		}

		String methodName = context.Identifier().getText();
		Class<?> owner = loadedClasses.remove();

		try
		{
			Class<?>[] argumentClasses =
				loadedClasses.toArray(new Class<?>[loadedClasses.size()]);

			owner.getMethod(methodName, argumentClasses);
		}
		catch (NoSuchMethodException ex)
		{
			String message =
				"No method " + methodName + "() on object of type " +
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
