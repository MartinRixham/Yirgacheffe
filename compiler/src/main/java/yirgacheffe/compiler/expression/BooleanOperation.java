package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.IntersectionType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class BooleanOperation implements Expression
{
	private int integerComparisonOpcode;

	private int referenceComparisonOpcode;

	private Expression firstOperand;

	private Expression secondOperand;

	public BooleanOperation(
		int comparisonOpcode,
		int referenceComparisonOpcode,
		Expression firstOperand,
		Expression secondOperand)
	{
		this.integerComparisonOpcode = comparisonOpcode;
		this.referenceComparisonOpcode = referenceComparisonOpcode;
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

		if (firstType.isAssignableTo(PrimitiveType.DOUBLE))
		{
			if (secondType.isAssignableTo(PrimitiveType.DOUBLE))
			{
				this.secondOperand.compile(methodVisitor, variables);
			}
			else
			{
				methodVisitor.visitInsn(Opcodes.DCONST_0);
			}

			this.firstOperand.compile(methodVisitor, variables);

			methodVisitor.visitInsn(Opcodes.DUP2);
			methodVisitor.visitInsn(Opcodes.DCONST_0);
			methodVisitor.visitInsn(Opcodes.DCMPL);
			methodVisitor.visitJumpInsn(this.integerComparisonOpcode, label);
			methodVisitor.visitInsn(Opcodes.DUP2_X2);
			methodVisitor.visitInsn(Opcodes.POP2);
			methodVisitor.visitLabel(label);
			methodVisitor.visitInsn(Opcodes.POP2);
		}
		else
		{
			if (secondType.isAssignableTo(PrimitiveType.DOUBLE))
			{
				methodVisitor.visitInsn(Opcodes.ICONST_0);
			}
			else
			{
				this.secondOperand.compile(methodVisitor, variables);
			}

			this.firstOperand.compile(methodVisitor, variables);

			methodVisitor.visitInsn(Opcodes.DUP);

			if (firstType instanceof PrimitiveType)
			{
				methodVisitor.visitJumpInsn(this.integerComparisonOpcode, label);
			}
			else
			{
				methodVisitor.visitJumpInsn(this.referenceComparisonOpcode, label);
			}

			methodVisitor.visitInsn(Opcodes.SWAP);
			methodVisitor.visitLabel(label);
			methodVisitor.visitInsn(Opcodes.POP);
		}

		return errors;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}
}
