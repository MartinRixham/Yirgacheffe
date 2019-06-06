package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class BooleanComparison implements Comparison
{
	private Coordinate coordinate;
	private Comparator comparator;
	private Expression firstOperand;
	private Expression secondOperand;

	public BooleanComparison(
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

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label)
	{
		Array<Error> errors = new Array<>();
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		if (secondType != PrimitiveType.BOOLEAN ||
			!(this.comparator instanceof Equals ||
			this.comparator instanceof NotEquals))
		{
			String message =
				"Cannot compare " + firstType + " and " + secondType + ".";

			errors.push(new Error(this.coordinate, message));

			return errors;
		}

		errors = errors.concat(this.firstOperand.compile(methodVisitor, variables));
		errors = errors.concat(this.secondOperand.compile(methodVisitor, variables));
		this.comparator.compile(methodVisitor, label, firstType);

		return errors;
	}
}
