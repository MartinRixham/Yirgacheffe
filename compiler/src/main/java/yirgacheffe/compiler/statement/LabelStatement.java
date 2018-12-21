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

public class LabelStatement implements Statement
{
	private Label label;

	public LabelStatement(Label label)
	{
		this.label = label;
	}

	@Override
	public boolean returns()
	{
		return false;
	}

	@Override
	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		methodVisitor.visitLabel(this.label);

		return new Array<>();
	}

	@Override
	public Expression getFirstOperand()
	{
		return new Nothing();
	}

	@Override
	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	@Override
	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	@Override
	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return true;
	}
}
