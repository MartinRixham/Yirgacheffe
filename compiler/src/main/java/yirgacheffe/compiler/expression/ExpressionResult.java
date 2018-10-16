package yirgacheffe.compiler.expression;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.lang.Array;

public class ExpressionResult
{
	private Array<Error> errors;

	public ExpressionResult(Error... error)
	{
		this.errors = new Array<>(error);
	}

	public ExpressionResult(Array<Error> errors)
	{
		this.errors = errors;
	}

	public Array<Error> getErrors()
	{
		return this.errors;
	}

	public ExpressionResult add(ExpressionResult other)
	{
		return new ExpressionResult(this.errors.concat(other.errors));
	}
}
