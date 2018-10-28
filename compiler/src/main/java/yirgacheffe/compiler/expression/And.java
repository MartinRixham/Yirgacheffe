package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class And implements Expression
{
	private Expression firstOperand;

	private Expression secondOperand;

	public And(Expression firstOperand, Expression secondOperand)
	{
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Type getType(Variables variables)
	{
		return this.firstOperand.getType(variables);
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = new Array<>();

		this.secondOperand.compile(methodVisitor, variables);
		this.firstOperand.compile(methodVisitor, variables);

		methodVisitor.visitInsn(Opcodes.DUP2);
		methodVisitor.visitInsn(Opcodes.DCONST_0);
		methodVisitor.visitInsn(Opcodes.DCMPL);

		Label label = new Label();

		methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
		methodVisitor.visitInsn(Opcodes.DUP2_X2);
		methodVisitor.visitInsn(Opcodes.POP2);
		methodVisitor.visitLabel(label);
		methodVisitor.visitInsn(Opcodes.POP2);

		return errors;
	}
}
