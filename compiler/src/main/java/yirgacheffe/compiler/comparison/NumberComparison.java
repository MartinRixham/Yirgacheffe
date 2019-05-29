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

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label)
	{
		Array<Error> errors = new Array<>();
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		if (!secondType.isAssignableTo(PrimitiveType.DOUBLE))
		{
			String message =
				"Cannot compare " + firstType + " and " + secondType + ".";

			errors.push(new Error(this.coordinate, message));

			return errors;
		}

		PrimitiveType firstPrimitive = (PrimitiveType) firstType;
		PrimitiveType secondPrimitive = (PrimitiveType) secondType;

		if (firstPrimitive.order() < secondPrimitive.order())
		{
			this.firstOperand.compile(methodVisitor, variables);

			methodVisitor.visitInsn(firstPrimitive.convertTo(secondPrimitive));

			this.secondOperand.compile(methodVisitor, variables);
			this.comparator.compile(methodVisitor, label, secondType);
		}
		else if (firstPrimitive.order() > secondPrimitive.order())
		{
			this.firstOperand.compile(methodVisitor, variables);
			this.secondOperand.compile(methodVisitor, variables);

			methodVisitor.visitInsn(secondPrimitive.convertTo(firstPrimitive));

			this.comparator.compile(methodVisitor, label, firstType);
		}
		else
		{
			this.firstOperand.compile(methodVisitor, variables);
			this.secondOperand.compile(methodVisitor, variables);
			this.comparator.compile(methodVisitor, label, firstType);
		}

		return errors;
	}
}
