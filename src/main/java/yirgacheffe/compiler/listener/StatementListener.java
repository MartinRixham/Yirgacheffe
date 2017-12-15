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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class StatementListener extends FieldListener
{
	private Variable currentVariable;

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

			this.typeStack.push(PrimitiveType.BOOL);
		}
		else
		{
			value = new Double(context.getText());

			this.typeStack.push(PrimitiveType.NUM);
		}

		this.methodVisitor.visitLdcInsn(value);
	}

	@Override
	public void enterVariableDeclaration(
		YirgacheffeParser.VariableDeclarationContext context)
	{
		Type type = this.types.getType(context.type());

		int index = 1;

		for (Variable variable: this.localVariables.values())
		{
			index += variable.getType().width();
		}

		this.currentVariable = new Variable(index, type);

		this.localVariables.put(context.Identifier().getText(), this.currentVariable);
	}

	@Override
	public void enterVariableWrite(YirgacheffeParser.VariableWriteContext context)
	{
		if (this.localVariables.containsKey(context.getText()))
		{
			this.currentVariable = this.localVariables.get(context.getText());
		}
		else
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
		int index = 0;

		if (this.currentVariable != null)
		{
			index = this.currentVariable.getIndex();
		}

		if (type.equals(PrimitiveType.NUM))
		{
			this.methodVisitor.visitVarInsn(Opcodes.DSTORE, index);
		}
		else if (type.equals(PrimitiveType.BOOL))
		{
			this.methodVisitor.visitVarInsn(Opcodes.ISTORE, index);
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
