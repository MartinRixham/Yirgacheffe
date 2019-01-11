package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
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

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Coordinate coordinate = new Coordinate(0, 0);
		Error error = new Error(coordinate, "This expression is not valid.");

		return new Array<>(error);
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}
}
