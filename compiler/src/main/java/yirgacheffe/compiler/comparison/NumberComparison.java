package yirgacheffe.compiler.comparison;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;

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

			return result.add(new Error(this.coordinate, message));
		}

		PrimitiveType firstPrimitive = (PrimitiveType) firstType;
		PrimitiveType secondPrimitive = (PrimitiveType) secondType;

		if (firstPrimitive.order() < secondPrimitive.order())
		{
			return result
				.add(new InsnNode(firstPrimitive.convertTo(secondPrimitive)))
				.concat(this.secondOperand.compile(variables))
				.concat(this.comparator.compile(label, secondType));
		}
		else if (firstPrimitive.order() > secondPrimitive.order())
		{
			return result
				.concat(this.secondOperand.compile(variables))
				.add(new InsnNode(secondPrimitive.convertTo(firstPrimitive)))
				.concat(this.comparator.compile(label, firstType));
		}
		else
		{
			return result
				.concat(this.secondOperand.compile(variables))
				.concat(this.comparator.compile(label, firstType));
		}
	}
}
