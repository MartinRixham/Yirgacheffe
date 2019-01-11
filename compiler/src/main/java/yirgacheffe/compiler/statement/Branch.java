package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
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

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		Label label = this.conditional.getLabel();

		Array<Error> errors =
			this.conditional.compile(
				methodVisitor,
				variables,
				caller);

		methodVisitor.visitLabel(label);

		return errors;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.conditional.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return this.conditional.getVariableWrites();
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
