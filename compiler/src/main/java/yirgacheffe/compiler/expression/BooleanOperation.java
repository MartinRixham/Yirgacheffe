package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.compiler.type.IntersectionType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class BooleanOperation implements Expression
{
	private BooleanOperator operator;

	private Expression firstOperand;

	private Expression secondOperand;

	public BooleanOperation(
		BooleanOperator operator,
		Expression firstOperand,
		Expression secondOperand)
	{
		this.operator = operator;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Type getType(Variables variables)
	{
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		return new IntersectionType(firstType, secondType);
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = new Array<>();
		Label label = new Label();
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		errors = errors.concat(this.firstOperand.compile(methodVisitor, variables));

		if (firstType.width() == 2)
		{
			methodVisitor.visitInsn(Opcodes.DUP2);
		}
		else
		{
			methodVisitor.visitInsn(Opcodes.DUP);
		}

		if (firstType.isPrimitive() && !secondType.isPrimitive())
		{
			this.compileBoxingCall(methodVisitor, firstType);

			if (firstType.width() == 2)
			{
				methodVisitor.visitInsn(Opcodes.DUP_X2);
				methodVisitor.visitInsn(Opcodes.POP);
			}
			else
			{
				methodVisitor.visitInsn(Opcodes.SWAP);
			}
		}

		this.compileComparison(methodVisitor, this.operator, label, firstType);

		if (firstType.width() == 2 && secondType.width() == 2)
		{
			methodVisitor.visitInsn(Opcodes.POP2);
		}
		else
		{
			methodVisitor.visitInsn(Opcodes.POP);
		}

		errors =
			errors.concat(
				this.secondOperand.compile(methodVisitor, variables));

		if (!firstType.isPrimitive() && secondType.isPrimitive())
		{
			this.compileBoxingCall(methodVisitor, secondType);
		}

		methodVisitor.visitLabel(label);

		return errors;
	}

	private void compileBoxingCall(MethodVisitor methodVisitor, Type type)
	{
		String descriptor =
			"(" + type.toJVMType() + ")L" +
				type.toFullyQualifiedType() + ";";

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESTATIC,
			type.toFullyQualifiedType(),
			"valueOf",
			descriptor,
			false);
	}

	public Array<Error> compileCondition(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label)
	{
		Array<Error> errors = new Array<>();
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		Label leftLabel;

		if (this.operator == BooleanOperator.OR)
		{
			leftLabel = new Label();
		}
		else
		{
			leftLabel = label;
		}

		errors = errors.concat(this.firstOperand.compile(methodVisitor, variables));

		this.compileComparison(
			methodVisitor,
			this.operator,
			leftLabel,
			firstType);

		errors =
			errors.concat(
				this.secondOperand.compileCondition(methodVisitor, variables, label));

		this.compileComparison(
			methodVisitor,
			BooleanOperator.AND,
			label,
			secondType);

		if (this.operator == BooleanOperator.OR)
		{
			methodVisitor.visitLabel(leftLabel);
		}

		return errors;
	}

	private void compileComparison(
		MethodVisitor methodVisitor,
		BooleanOperator operator,
		Label label,
		Type type)
	{
		if (type.equals(PrimitiveType.DOUBLE))
		{
			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"yirgacheffe/lang/Falsyfier",
				"isTruthy",
				"(D)Z",
				false);

			methodVisitor.visitJumpInsn(operator.integerOpcode(), label);
		}
		else if (type.isPrimitive())
		{
			methodVisitor.visitJumpInsn(operator.integerOpcode(), label);
		}
		else if (type.isAssignableTo(new ReferenceType(String.class)))
		{
			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"yirgacheffe/lang/Falsyfier",
				"isTruthy",
				"(Ljava/lang/String;)Z",
				false);

			methodVisitor.visitJumpInsn(operator.integerOpcode(), label);
		}
		else
		{
			methodVisitor.visitJumpInsn(operator.referenceOpcode(), label);
		}
	}

	public boolean isCondition(Variables variables)
	{
		return true;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}
}
