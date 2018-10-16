package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.lang.Array;

public class StatementResult
{
	private boolean returns;

	private Array<Error> errors;

	public StatementResult(boolean returns, Error... error)
	{
		this.returns = returns;
		this.errors = new Array<>(error);
	}

	public StatementResult(boolean returns, Array<Error> errors)
	{
		this.returns = returns;
		this.errors = errors;
	}

	public boolean returns()
	{
		return this.returns;
	}

	public StatementResult add(StatementResult other)
	{
		boolean returns = this.returns || other.returns;

		return new StatementResult(returns, this.errors.concat(other.errors));
	}

	public Array<Error> getErrors()
	{
		return this.errors;
	}
}
