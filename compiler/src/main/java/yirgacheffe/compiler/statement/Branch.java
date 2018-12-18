package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Branch implements Statement
{
	private ConditionalStatement conditional;

	public Branch(ConditionalStatement conditional)
	{
		this.conditional = conditional;
	}

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

	public Expression getFirstOperand()
	{
		return this.conditional.getFirstOperand();
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.conditional.getVariableReads();
	}
}
