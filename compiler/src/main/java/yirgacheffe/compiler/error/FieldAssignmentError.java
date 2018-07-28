package yirgacheffe.compiler.error;

import yirgacheffe.compiler.type.Type;

public class FieldAssignmentError implements ErrorMessage
{
	private Type type;

	private Type expressionType;

	public FieldAssignmentError(Type type, Type expressionType)
	{
		this.type = type;
		this.expressionType = expressionType;
	}

	@Override
	public String toString()
	{
		return
			"Cannot assign expression of type " + this.expressionType +
			" to field of type " + this.type + ".";
	}
}
