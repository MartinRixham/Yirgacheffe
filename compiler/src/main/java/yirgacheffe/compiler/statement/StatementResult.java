package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.lang.Array;

public class StatementResult
{
	private Array<Error> errors;

	public StatementResult(Error... error)
	{
		this.errors = new Array<>(error);
	}

	public StatementResult(Array<Error> errors)
	{
		this.errors = errors;
	}

	public StatementResult add(StatementResult other)
	{
		return new StatementResult(this.errors.concat(other.errors));
	}

	public Array<Error> getErrors()
	{
		return this.errors;
	}
}
