package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Type;

public class FunctionCall implements Statement
{
	private Expression expression;

	public FunctionCall(Expression expression)
	{
		this.expression = expression;
	}

	@Override
	public boolean compile(MethodVisitor methodVisitor, StatementResult result)
	{
		Type type = this.expression.check(result);

		this.expression.compile(methodVisitor);

		int width = type.width();

		if (width == 1)
		{
			methodVisitor.visitInsn(Opcodes.POP);
		}
		else if (width == 2)
		{
			methodVisitor.visitInsn(Opcodes.POP2);
		}

		return false;
	}
}
