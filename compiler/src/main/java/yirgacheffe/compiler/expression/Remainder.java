package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Remainder implements Expression
{
	private Coordinate coordinate;

	private Expression firstOperand;

	private Expression secondOperand;

	public Remainder(
		Coordinate coordinate,
		Expression firstOperand,
		Expression secondOperand)
	{
		this.coordinate = coordinate;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.DOUBLE;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = new Array<>();

		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		if (firstType != PrimitiveType.DOUBLE || secondType != PrimitiveType.DOUBLE)
		{
			String message =
				"Cannot find remainder of types " +
				firstType + " and " + secondType + ".";

			errors.push(new Error(this.coordinate, message));
		}

		errors.concat(this.firstOperand.compile(methodVisitor, variables));
		errors.concat(this.secondOperand.compile(methodVisitor, variables));

		methodVisitor.visitInsn(Opcodes.DREM);

		return errors;
	}
}
