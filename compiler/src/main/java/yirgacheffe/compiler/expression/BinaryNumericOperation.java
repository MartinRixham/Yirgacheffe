package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
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
		return this.firstOperand.getType(variables);
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = new Array<>();

		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		if (firstType != PrimitiveType.DOUBLE || secondType != PrimitiveType.DOUBLE)
		{
			String message =
				"Cannot " + this.operator.getDescription() + " " +
				firstType + " and " + secondType + ".";

			errors.push(new Error(this.coordinate, message));
		}

		errors.push(this.firstOperand.compile(methodVisitor, variables));
		errors.push(this.secondOperand.compile(methodVisitor, variables));

		methodVisitor.visitInsn(this.operator.getOpcode());

		return errors;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}
}
