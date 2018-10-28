package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Equation implements Expression
{
	private Expression firstOperand;

	private Expression secondOperand;

	public Equation(Expression firstOperand, Expression secondOperand)
	{
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

		this.firstOperand.compile(methodVisitor, variables);
		this.secondOperand.compile(methodVisitor, variables);

		Label trueLabel = new Label();

		if (this.firstOperand.getType(variables) == PrimitiveType.DOUBLE)
		{
			methodVisitor.visitInsn(Opcodes.DCMPL);
			methodVisitor.visitJumpInsn(Opcodes.IFEQ, trueLabel);
		}
		else
		{
			methodVisitor.visitJumpInsn(Opcodes.IF_ICMPEQ, trueLabel);
		}

		methodVisitor.visitInsn(Opcodes.ICONST_0);

		Label falseLabel = new Label();

		methodVisitor.visitJumpInsn(Opcodes.GOTO, falseLabel);
		methodVisitor.visitLabel(trueLabel);
		methodVisitor.visitInsn(Opcodes.ICONST_1);
		methodVisitor.visitLabel(falseLabel);

		return errors;
	}
}
