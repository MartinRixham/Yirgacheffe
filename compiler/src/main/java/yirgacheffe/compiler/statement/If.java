package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.expression.Expression;

public class If implements Statement
{
	private Expression condition;

	private Statement statement;

	public If(Expression condition, Statement statement)
	{
		this.condition = condition;
		this.statement = statement;
	}

	@Override
	public void compile(MethodVisitor methodVisitor, StatementResult result)
	{
		this.condition.check(result);
		this.condition.compile(methodVisitor);

		Label label = new Label();

		methodVisitor.visitJumpInsn(Opcodes.IFEQ, label);

		this.statement.compile(methodVisitor, result);

		methodVisitor.visitLabel(label);
	}
}
