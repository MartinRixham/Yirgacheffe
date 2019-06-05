package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.operator.Operator;
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
		else if (firstType.equals(PrimitiveType.DOUBLE) ||
			secondType.equals(PrimitiveType.DOUBLE))
		{
			return PrimitiveType.DOUBLE;
		}
		else if (firstType.equals(PrimitiveType.LONG) ||
			secondType.equals(PrimitiveType.LONG))
		{
			return PrimitiveType.LONG;
		}
		else
		{
			return PrimitiveType.INT;
		}
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);
		Type string = new ReferenceType(String.class);

		if (this.operator == Operator.ADD  &&
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
			errors.push(this.firstOperand.compile(methodVisitor, variables));

			return errors;
		}

		PrimitiveType type = (PrimitiveType) this.getType(variables);
		PrimitiveType firstPrimitive = (PrimitiveType) firstType;
		PrimitiveType secondPrimitive = (PrimitiveType) secondType;

		errors.push(this.firstOperand.compile(methodVisitor, variables));

		if (firstPrimitive != type)
		{
			methodVisitor.visitInsn(firstPrimitive.convertTo(type));
		}

		errors = errors.concat(this.secondOperand.compile(methodVisitor, variables));

		if (secondPrimitive != type)
		{
			methodVisitor.visitInsn(secondPrimitive.convertTo(type));
		}

		if (type == PrimitiveType.INT)
		{
			methodVisitor.visitInsn(this.operator.getIntOpcode());
		}
		else if (type == PrimitiveType.LONG)
		{
			methodVisitor.visitInsn(this.operator.getLongOpcode());
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

		errors = errors.concat(this.secondOperand.compile(methodVisitor, variables));

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
