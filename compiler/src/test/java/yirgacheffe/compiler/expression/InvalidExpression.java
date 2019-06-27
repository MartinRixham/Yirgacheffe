package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class InvalidExpression implements Expression
{
	private Type type;

	public InvalidExpression(Type type)
	{
		this.type = type;
	}

	public Type getType(Variables variables)
	{
		return this.type;
	}

	public Result compile(Variables variables)
	{
		Coordinate coordinate = new Coordinate(0, 0);
		Error error = new Error(coordinate, "This expression is not valid.");

		return new Result().add(error);
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return new Result();
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}
}
