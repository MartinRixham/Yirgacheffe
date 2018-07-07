package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.expression.Expression;

public class Return implements Statement
{
	private Expression expression;

	public Return(Expression expression)
	{
		this.expression = expression;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		this.expression.compile(methodVisitor);
	}
}
