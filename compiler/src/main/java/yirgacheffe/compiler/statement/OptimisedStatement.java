package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class OptimisedStatement implements Statement
{
	private Statement statement;

	public OptimisedStatement(Statement statement)
	{
		this.statement = statement;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Result result = new Result();
		Array<Error> errors = this.statement.compile(variables, caller).getErrors();

		for (Error error: errors)
		{
			result = result.add(error);
		}

		return result;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.statement.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public Expression getExpression()
	{
		return this.statement.getExpression();
	}

	public boolean isEmpty()
	{
		return true;
	}
}
