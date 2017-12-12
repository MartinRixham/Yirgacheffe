package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.Type;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Stack;

public class StatementListener extends FieldListener
{
	private Stack<String> typeStack = new Stack<>();

	public StatementListener(
		String sourceFile,
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(sourceFile, types, classLoader, errorListener, writer);
	}

	@Override
	public void enterLiteral(YirgacheffeParser.LiteralContext context)
	{
		Object value;

		if (context.StringLiteral() != null)
		{
			value = context.getText().replace("\"", "");

			this.typeStack.push("java.lang.String");
		}
		else if (context.CharacterLiteral() != null)
		{
			value = context.getText().charAt(1);
		}
		else if (context.BooleanLiteral() != null)
		{
			value = context.getText().equals("true");
		}
		else
		{
			value = new Double(context.getText());
		}

		this.methodVisitor.visitLdcInsn(value);
	}

	@Override
	public void exitVariableInitialisation(
		YirgacheffeParser.VariableInitialisationContext context)
	{
		this.methodVisitor.visitVarInsn(Opcodes.DSTORE, 1);
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

		this.typeStack.push(type.toFullyQualifiedType());
	}

	@Override
	public void exitMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String type = this.typeStack.pop();
		String methodName = context.Identifier().getText();

		Class<?> loadedClass;

		try
		{
			loadedClass = this.classLoader.loadClass(type);
		}
		catch (ClassNotFoundException e)
		{
			try
			{
				loadedClass = classLoader.loadClass(type);

			}
			catch (ClassNotFoundException ex)
			{
				throw new RuntimeException(ex);
			}
		}

		try
		{
			loadedClass.getMethod(methodName);
		}
		catch (NoSuchMethodException ex)
		{
			String message =
				"No method " + methodName + "() on object of type " + type + ".";

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
