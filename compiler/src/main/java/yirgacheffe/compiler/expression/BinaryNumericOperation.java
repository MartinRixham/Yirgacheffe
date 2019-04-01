package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class BinaryNumericOperation implements Expression
{
	private Coordinate coordinate;

	private Operator operator;

	private Expression firstOperand;

	private Expression secondOperand;

	public BinaryNumericOperation(
		Coordinate coordinate,
		Operator operator,
		Expression firstOperand,
		Expression secondOperand)
	{
		this.coordinate = coordinate;
		this.operator = operator;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Type getType(Variables variables)
	{
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		if (firstType == PrimitiveType.INT && secondType == PrimitiveType.INT)
		{
			return PrimitiveType.INT;
		}
		else if (firstType.isAssignableTo(PrimitiveType.DOUBLE) &&
			secondType.isAssignableTo(PrimitiveType.DOUBLE))
		{
			return PrimitiveType.DOUBLE;
		}
		else
		{
			return firstType;
		}
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = new Array<>();

		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		if (!firstType.isAssignableTo(PrimitiveType.DOUBLE) ||
			!secondType.isAssignableTo(PrimitiveType.DOUBLE))
		{
			String message =
				"Cannot " + this.operator.getDescription() + " " +
				firstType + " and " + secondType + ".";

			errors.push(new Error(this.coordinate, message));
		}

		Type type = this.getType(variables);

		errors.push(this.firstOperand.compile(methodVisitor, variables));

		if (type != firstType)
		{
			methodVisitor.visitInsn(Opcodes.I2D);
		}

		errors.push(this.secondOperand.compile(methodVisitor, variables));

		if (type != secondType)
		{
			methodVisitor.visitInsn(Opcodes.I2D);
		}

		if (type == PrimitiveType.INT)
		{
			methodVisitor.visitInsn(this.operator.getIntOpcode());
		}
		else
		{
			methodVisitor.visitInsn(this.operator.getDoubleOpcode());
		}

		return errors;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}
}
