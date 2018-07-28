package yirgacheffe.compiler.error;

import yirgacheffe.compiler.type.Type;

public class ReturnTypeError implements ErrorMessage
{
	private Type returnType;

	private Type expressionType;

	public ReturnTypeError(Type returnType, Type expressionType)
	{
		this.returnType = returnType;
		this.expressionType = expressionType;
	}

	@Override
	public String toString()
	{
		return
			"Mismatched return type: Cannot return expression of type " +
			this.expressionType + " from method of return type " +
			this.returnType + ".";
	}
}
