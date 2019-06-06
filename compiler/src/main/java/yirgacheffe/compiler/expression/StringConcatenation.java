package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class StringConcatenation implements Expression
{
	private BinaryOperation binaryOperation;

	public StringConcatenation(BinaryOperation expression)
	{
		this.binaryOperation = expression;
	}

	@Override
	public Type getType(Variables variables)
	{
		return this.binaryOperation.getType(variables);
	}

	@Override
	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Type string = new ReferenceType(String.class);

		if (this.binaryOperation.getType(variables).isAssignableTo(string))
		{
			new Array<>();

			methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
			methodVisitor.visitInsn(Opcodes.DUP);

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESPECIAL,
				"java/lang/StringBuilder",
				"<init>",
				"()V",
				false);

			Array<Error> errors = this.binaryOperation.compile(methodVisitor, variables);

			String secondOperandType =
				this.binaryOperation.getSecondOperandType(variables);

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/StringBuilder",
				"append",
				"(" + secondOperandType + ")Ljava/lang/StringBuilder;",
				false);

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/StringBuilder",
				"toString",
				"()Ljava/lang/String;",
				false);

			return errors;
		}
		else
		{
			return this.binaryOperation.compile(methodVisitor, variables);
		}
	}

	public Array<Error> compileCondition(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label)
	{
		return this.compile(methodVisitor, variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.binaryOperation.getVariableReads();
	}
}
