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

		if (firstType.equals(PrimitiveType.DOUBLE))
		{
			this.firstOperand.compile(methodVisitor, variables);

			methodVisitor.visitInsn(Opcodes.DUP2);
			methodVisitor.visitInsn(Opcodes.DCONST_0);
			methodVisitor.visitInsn(Opcodes.DCMPL);
			methodVisitor.visitJumpInsn(this.integerComparisonOpcode, label);
			methodVisitor.visitInsn(Opcodes.POP2);

			this.secondOperand.compile(methodVisitor, variables);

			methodVisitor.visitLabel(label);
		}
		else
		{
			this.firstOperand.compile(methodVisitor, variables);

			methodVisitor.visitInsn(Opcodes.DUP);

			if (firstType.isPrimitive())
			{
				methodVisitor.visitJumpInsn(this.integerComparisonOpcode, label);
			}
			else
			{
				methodVisitor.visitJumpInsn(this.referenceComparisonOpcode, label);
			}

			methodVisitor.visitInsn(Opcodes.POP);

			this.secondOperand.compile(methodVisitor, variables);

			methodVisitor.visitLabel(label);
		}

		return errors;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}
}
