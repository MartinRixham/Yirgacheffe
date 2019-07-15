package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.Result;
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

	public Result compile(Variables variables, Signature caller)
	{
		return new Result();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return true;
	}
}
