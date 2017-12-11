package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.Type.Types;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.parser.YirgacheffeParser;

public class StatementListener extends FieldListener
{
	public StatementListener(
		String directory,
		Types types,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(directory, types, classLoader, errorListener, writer);
	}

	@Override
	public void enterLiteral(YirgacheffeParser.LiteralContext context)
	{
		Object value;

		if (context.StringLiteral() != null)
		{
			value = context.getText().replace("\"", "");
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
		this.methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/String");
		this.methodVisitor.visitInsn(Opcodes.DUP);

		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/String",
			"<init>",
			"()V",
			false);

		this.methodVisitor.visitInsn(Opcodes.POP);
	}

	@Override
	public void exitMethodCall(YirgacheffeParser.MethodCallContext context)
	{
		this.methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			"java/lang/String",
			"toString",
			"()Ljava/lang/String;",
			false);

		this.methodVisitor.visitInsn(Opcodes.POP);
	}
}
