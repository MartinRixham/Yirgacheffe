package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class StringComparison implements Comparison
{
	private Coordinate coordinate;
	private Comparator comparator;
	private Expression firstOperand;
	private Expression secondOperand;

	public StringComparison(
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

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label)
	{
		Array<Error> errors = new Array<>();

		errors = errors.concat(this.firstOperand.compile(methodVisitor, variables));
		errors = errors.concat(this.secondOperand.compile(methodVisitor, variables));

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			"java/lang/String",
			"equals",
			"(Ljava/lang/Object;)Z",
			false);

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
}
