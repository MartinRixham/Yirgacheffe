package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;

public class ObjectComparison implements Comparison
{
	private Coordinate coordinate;
	private Comparator comparator;
	private Expression firstOperand;
	private Expression secondOperand;

	public ObjectComparison(
		Coordinate coordinate,
		Comparator comparator,
		Expression firstOperand,
		Expression secondOperand)
	{
		this.coordinate = coordinate;
		this.comparator = comparator;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Result compile(Variables variables, Label label)
	{
		Result result = new Result();
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		if (secondType.isPrimitive() ||
			!(this.comparator instanceof Equals ||
			this.comparator instanceof NotEquals))
		{
			String message =
				"Cannot compare " + firstType + " and " + secondType + ".";

			return result.add(new Error(this.coordinate, message));
		}

		return result
			.concat(this.firstOperand.compile(variables))
			.concat(this.secondOperand.compile(variables))
			.concat(this.comparator.compile(label, firstType));
	}
}
