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

		this.firstOperand.compile(methodVisitor, variables);

		if (firstType.width() == 2)
		{
			methodVisitor.visitInsn(Opcodes.DUP2);
		}
		else
		{
			methodVisitor.visitInsn(Opcodes.DUP);
		}

		this.compileComparison(methodVisitor, label, firstType);

		if (firstType.width() == 2)
		{
			methodVisitor.visitInsn(Opcodes.POP2);
		}
		else
		{
			methodVisitor.visitInsn(Opcodes.POP);
		}

		this.secondOperand.compile(methodVisitor, variables);

		methodVisitor.visitLabel(label);

		return errors;
	}

	public Array<Error> compileCondition(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label)
	{
		Array<Error> errors = new Array<>();
		Type firstType = this.firstOperand.getType(variables);

		this.firstOperand.compile(methodVisitor, variables);

		this.compileComparison(methodVisitor, label, firstType);

		this.secondOperand.compile(methodVisitor, variables);

		return errors;
	}

	private void compileComparison(
		MethodVisitor methodVisitor,
		Label label,
		Type firstType)
	{
		if (firstType.equals(PrimitiveType.DOUBLE))
		{
			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"yirgacheffe/lang/Falsyfier",
				"isTruthy",
				"(D)Z",
				false);

			methodVisitor.visitJumpInsn(this.operator.integerOpcode(), label);
		}
		else if (firstType.isPrimitive())
		{
			methodVisitor.visitJumpInsn(this.operator.integerOpcode(), label);
		}
		else if (firstType.isAssignableTo(new ReferenceType(String.class)))
		{
			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"yirgacheffe/lang/Falsyfier",
				"isTruthy",
				"(Ljava/lang/String;)Z",
				false);

			methodVisitor.visitJumpInsn(this.operator.integerOpcode(), label);
		}
		else
		{
			methodVisitor.visitJumpInsn(this.operator.referenceOpcode(), label);
		}
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}
}
