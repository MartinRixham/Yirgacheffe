package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Negation implements Expression
{
	private Coordinate coordinate;

	private Expression expression;

	public Negation(Coordinate coordinate, Expression expression)
	{
		this.coordinate = coordinate;
		this.expression = expression;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.DOUBLE;
	}

	public Result compile(Variables variables)
	{
		Result result = this.expression.compile(variables);
		Type type = this.expression.getType(variables);

		if (!type.isAssignableTo(PrimitiveType.DOUBLE))
		{
			String message = "Cannot negate " + type + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		result = result.add(new InsnNode(Opcodes.DNEG));

		return result;
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
		return this.expression.getVariableReads();
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}
}
