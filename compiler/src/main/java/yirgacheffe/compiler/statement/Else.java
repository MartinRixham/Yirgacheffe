package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Else implements ConditionalStatement
{
	private Statement precondition;

	private Statement statement;

	private Label label = new Label();

	public Else(Statement precondition, Statement statement)
	{
		this.precondition = precondition;
		this.statement = statement;
	}

	@Override
	public void compile(MethodVisitor methodVisitor, StatementResult result)
	{
		this.precondition.compile(methodVisitor, result);

		methodVisitor.visitJumpInsn(Opcodes.GOTO, this.label);

		If ifStatement = (If) this.precondition;

		methodVisitor.visitLabel(ifStatement.getLabel());

		this.statement.compile(methodVisitor, result);
	}

	@Override
	public Label getLabel()
	{
		return this.label;
	}
}
