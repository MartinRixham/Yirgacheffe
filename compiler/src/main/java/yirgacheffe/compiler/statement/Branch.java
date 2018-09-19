package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class Branch implements Statement
{
	private ConditionalStatement conditional;

	public Branch(ConditionalStatement conditional)
	{
		this.conditional = conditional;
	}

	@Override
	public boolean compile(MethodVisitor methodVisitor, StatementResult statementResult)
	{
		Label label = this.conditional.getLabel();

		boolean returns = this.conditional.compile(methodVisitor, statementResult);

		methodVisitor.visitLabel(label);

		return returns && (this.conditional instanceof Else);
	}
}
