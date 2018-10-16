package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Variables;

public class Branch implements Statement
{
	private ConditionalStatement conditional;

	public Branch(ConditionalStatement conditional)
	{
		this.conditional = conditional;
	}

	@Override
	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		Label label = this.conditional.getLabel();

		StatementResult result = this.conditional.compile(methodVisitor, variables);

		methodVisitor.visitLabel(label);

		boolean returns = result.returns() && (this.conditional instanceof Else);

		return new StatementResult(returns, result.getErrors());
	}
}
