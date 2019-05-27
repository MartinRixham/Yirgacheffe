package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.comparison.BooleanComparison;
import yirgacheffe.compiler.comparison.Comparator;
import yirgacheffe.compiler.comparison.Comparison;
import yirgacheffe.compiler.comparison.Equals;
import yirgacheffe.compiler.comparison.NotEquals;
import yirgacheffe.compiler.comparison.NumberComparison;
import yirgacheffe.compiler.comparison.ObjectComparison;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
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
		Array<Error> errors = new Array<>();
		Type string = new ReferenceType(String.class);

		if (this.firstOperand.getType(variables).isAssignableTo(string) &&
			this.secondOperand.getType(variables).isAssignableTo(string) &&
			(this.comparator instanceof Equals))
		{
			errors = errors.concat(this.compareStrings(
				methodVisitor,
				variables,
				this.firstOperand,
				this.secondOperand));

			return errors;
		}

		Label trueLabel	= new Label();

		errors = this.compileComparison(methodVisitor, variables, trueLabel);

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
		Array<Error> errors = new Array<>();
		Type string = new ReferenceType(String.class);
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		if (firstType.isAssignableTo(string) && secondType.isAssignableTo(string) &&
			(this.comparator instanceof Equals || this.comparator instanceof NotEquals))
		{
			errors = errors.concat(
				this.compareStrings(
					methodVisitor,
					variables,
					this.firstOperand,
					this.secondOperand));

			if (this.comparator instanceof Equals)
			{
				methodVisitor.visitJumpInsn(Opcodes.IFEQ, label);
			}
			else
			{
				methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
			}

			return errors;
		}

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

		errors = errors.concat(comparison.compile(methodVisitor, variables, label));

		return errors;
	}

	private Array<Error> compareStrings(
		MethodVisitor methodVisitor,
		Variables variables,
		Expression firstOperand,
		Expression secondOperand)
	{
		Array<Error> errors = new Array<>();

		errors = errors.concat(firstOperand.compile(methodVisitor, variables));
		errors = errors.concat(secondOperand.compile(methodVisitor, variables));

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			"java/lang/String",
			"equals",
			"(Ljava/lang/Object;)Z",
			false);

		return errors;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}
}
