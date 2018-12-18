package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class DoNothing implements Statement
{
	@Override
	public boolean returns()
	{
		return false;
	}

	@Override
	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		return new StatementResult();
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
}
