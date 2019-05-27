package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.comparison.BooleanComparison;
import yirgacheffe.compiler.comparison.Comparator;
import yirgacheffe.compiler.comparison.Comparison;
import yirgacheffe.compiler.comparison.NumberComparison;
import yirgacheffe.compiler.comparison.ObjectComparison;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Equation implements Expression
{
	private Coordinate coordinate;

	private Comparator comparator;

	private Expression firstOperand;

	private Expression secondOperand;

	public Equation(
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

	public Type getType(Variables variables)
	{
		return PrimitiveType.BOOLEAN;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Label trueLabel	= new Label();

		Array<Error> errors =
			this.compileComparison(methodVisitor, variables, trueLabel);

		methodVisitor.visitInsn(Opcodes.ICONST_1);

		Label falseLabel = new Label();

		methodVisitor.visitJumpInsn(Opcodes.GOTO, falseLabel);
		methodVisitor.visitLabel(trueLabel);
		methodVisitor.visitInsn(Opcodes.ICONST_0);
		methodVisitor.visitLabel(falseLabel);

		return errors;
	}

	public Array<Error> compileComparison(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label)
	{
		Type firstType = this.firstOperand.getType(variables);
		Comparison comparison;

		if (firstType == PrimitiveType.BOOLEAN)
		{
			comparison =
				new BooleanComparison(
					this.coordinate,
					this.comparator,
					this.firstOperand,
					this.secondOperand);
		}
		else if (firstType.isAssignableTo(PrimitiveType.DOUBLE))
		{
			comparison =
				new NumberComparison(
					this.coordinate,
					this.comparator,
					this.firstOperand,
					this.secondOperand);
		}
		else
		{
			comparison =
				new ObjectComparison(
					this.coordinate,
					this.comparator,
					this.firstOperand,
					this.secondOperand);
		}

		return comparison.compile(methodVisitor, variables, label);
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}
}
