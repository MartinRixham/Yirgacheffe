package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class DoNothing implements Statement
{
	public boolean returns()
	{
		return false;
	}

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		return new Array<>();
	}

	public Array<Expression> getCommutableOperands()
	{
		return new Array<>();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	@Override
	public Expression getExpression()
	{
		return new Nothing();
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}
}
