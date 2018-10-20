package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Variables;

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

	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		this.condition.getType(variables);
		this.condition.compile(methodVisitor, variables);

		methodVisitor.visitJumpInsn(Opcodes.IFEQ, this.label);

		return this.statement.compile(methodVisitor, variables);
	}

	public Label getLabel()
	{
		return this.label;
	}
}
