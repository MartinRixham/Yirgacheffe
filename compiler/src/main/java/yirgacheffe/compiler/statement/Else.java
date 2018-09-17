package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Else implements Statement
{
	private Statement precondition;

	private Statement statement;

	public Else(Statement precondition, Statement statement)
	{
		this.precondition = precondition;
		this.statement = statement;
	}

	@Override
	public void compile(MethodVisitor methodVisitor, StatementResult result)
	{
		this.precondition.compile(methodVisitor, result);

		Label label = new Label();

		methodVisitor.visitJumpInsn(Opcodes.GOTO, label);

		If ifStatement = (If) this.precondition;

		methodVisitor.visitLabel(ifStatement.getLabel());

		this.statement.compile(methodVisitor, result);

		methodVisitor.visitLabel(label);
	}
}
