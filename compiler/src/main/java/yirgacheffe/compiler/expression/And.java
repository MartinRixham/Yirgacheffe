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
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		return new IntersectionType(firstType, secondType);
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = new Array<>();
		Label label = new Label();
		Type type = this.firstOperand.getType(variables);

		this.secondOperand.compile(methodVisitor, variables);
		this.firstOperand.compile(methodVisitor, variables);

		if (type.isAssignableTo(PrimitiveType.DOUBLE))
		{
			methodVisitor.visitInsn(Opcodes.DUP2);
			methodVisitor.visitInsn(Opcodes.DCONST_0);
			methodVisitor.visitInsn(Opcodes.DCMPL);
			methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
			methodVisitor.visitInsn(Opcodes.DUP2_X2);
			methodVisitor.visitInsn(Opcodes.POP2);
			methodVisitor.visitLabel(label);
			methodVisitor.visitInsn(Opcodes.POP2);
		}
		else
		{
			methodVisitor.visitInsn(Opcodes.DUP);

			if (type instanceof PrimitiveType)
			{
				methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
			}
			else
			{
				methodVisitor.visitJumpInsn(Opcodes.IFNONNULL, label);
			}

			methodVisitor.visitInsn(Opcodes.SWAP);
			methodVisitor.visitLabel(label);
			methodVisitor.visitInsn(Opcodes.POP);
		}

		return errors;
	}
}
