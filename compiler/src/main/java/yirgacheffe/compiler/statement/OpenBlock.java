package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class OpenBlock implements Statement
{
	public boolean returns()
	{
		return false;
	}

	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		return new StatementResult();
	}

	public Expression getFirstOperand()
	{
		return new Nothing();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	@Override
	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}
}
