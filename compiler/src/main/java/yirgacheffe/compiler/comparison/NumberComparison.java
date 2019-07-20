package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;

public class NumberComparison implements Comparison
{
	private Coordinate coordinate;
	private Comparator comparator;
	private Expression firstOperand;
	private Expression secondOperand;

	public NumberComparison(
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
		Result result = this.firstOperand.compile(variables);
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		if (!secondType.isAssignableTo(PrimitiveType.DOUBLE))
		{
			String message =
				"Cannot compare " + firstType + " and " + secondType + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		Type type = firstType.intersect(secondType);

		return result
			.concat(firstType.convertTo(type))
			.concat(this.secondOperand.compile(variables))
			.concat(secondType.convertTo(type))
			.concat(this.comparator.compile(label, type));
	}
}
