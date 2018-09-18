package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.expression.Expression;

public class If implements ConditionalStatement
{
	private Expression condition;

	private Statement statement;

	private Label label = new Label();

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

		methodVisitor.visitJumpInsn(Opcodes.IFEQ, this.label);

		this.statement.compile(methodVisitor, result);
	}

	public Label getLabel()
	{
		return this.label;
	}
}
