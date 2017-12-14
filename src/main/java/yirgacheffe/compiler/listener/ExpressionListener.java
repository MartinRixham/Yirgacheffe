package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.ParseErrorListener;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.util.Map;

public class ExpressionListener extends StatementListener
{
	public ExpressionListener(
		String sourceFile,
		Map<String, Type> declaredTypes,
		BytecodeClassLoader classLoader,
		ParseErrorListener errorListener,
		ClassWriter writer)
	{
		super(sourceFile, declaredTypes, classLoader, errorListener, writer);
	}

	@Override
	public void enterVariableRead(YirgacheffeParser.VariableReadContext context)
	{
		if (this.localVariables.containsKey(context.getText()))
		{
			this.typeStack.push(this.localVariables.get(context.getText()).getType());
		}

		this.methodVisitor.visitVarInsn(Opcodes.DLOAD, 1);
	}
}
