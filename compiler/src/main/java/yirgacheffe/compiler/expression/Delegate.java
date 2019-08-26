package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Delegate implements Expression
{
	private Array<Expression> arguments;

	public Delegate(Array<Expression> arguments)
	{
		this.arguments = arguments;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.VOID;
	}

	public Result compile(Variables variables)
	{
		Type type = this.getType(variables);
		Type argumentType = this.arguments.get(0).getType(variables);

		variables.stackPush(type);
		variables.delegate(this, argumentType);

		return new Result();
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
