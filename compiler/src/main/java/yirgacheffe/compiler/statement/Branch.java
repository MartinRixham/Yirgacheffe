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
	public boolean returns()
	{
		return this.conditional.returns() && (this.conditional instanceof Else);
	}

	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		Label label = this.conditional.getLabel();

		StatementResult result = this.conditional.compile(methodVisitor, variables);

		methodVisitor.visitLabel(label);

		return new StatementResult(result.getErrors());
	}
}
