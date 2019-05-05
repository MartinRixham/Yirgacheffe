package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class BinaryOperation implements Expression
{
	private Coordinate coordinate;

	private Operator operator;

	private Expression firstOperand;

	private Expression secondOperand;

	public BinaryOperation(
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
		Type string = new ReferenceType(String.class);

		if (firstType.isAssignableTo(string) || secondType.isAssignableTo(string))
		{
			return string;
		}
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
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);
		Type string = new ReferenceType(String.class);

		if (
			this.operator == Operator.ADD  &&
			(firstType.isAssignableTo(string) || secondType.isAssignableTo(string)))
		{
			return this.compileStrings(methodVisitor, variables);
		}

		Array<Error> errors = new Array<>();

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

	private Array<Error> compileStrings(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = this.firstOperand.compile(methodVisitor, variables);

		Type firstOperandType = this.firstOperand.getType(variables);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			"java/lang/StringBuilder",
			"append",
			"(" + firstOperandType.toJVMType() + ")Ljava/lang/StringBuilder;",
			false);

		errors.push(this.secondOperand.compile(methodVisitor, variables));

		return errors;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}

	public String getSecondOperandType(Variables variables)
	{
		return this.secondOperand.getType(variables).toJVMType();
	}
}
