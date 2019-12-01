package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Nothing implements Expression
{
	public Type getType(Variables variables)
	{
		return PrimitiveType.VOID;
	}

	public Result compile(Variables variables)
	{
		variables.stackPush(this.getType(variables));

		return new Result();
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Coordinate getCoordinate()
	{
		return new Coordinate(0, 0);
	}
}
