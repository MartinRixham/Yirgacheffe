package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Try implements Expression
{
	private Expression expression;

	public Try(Expression expression)
	{
		this.expression = expression;
	}

	public Type getType(Variables variables)
	{
		return this.expression.getType(variables);
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = this.expression.compile(methodVisitor, variables);
		Type type = this.expression.getType(variables);

		/*if (type.isPrimitive())
		{
			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				type.toFullyQualifiedType(),
				"valueOf",
				"(" + type.toJVMType() + ")L" + type.toFullyQualifiedType() + ";",
				false);
		}*/

		return errors;
	}

	public Array<Error> compileCondition(
		MethodVisitor methodVisitor,
		Variables variables,
		Label trueLabel,
		Label falseLabel)
	{
		throw new UnsupportedOperationException();
	}

	public boolean isCondition(Variables variables)
	{
		throw new UnsupportedOperationException();
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}
}
