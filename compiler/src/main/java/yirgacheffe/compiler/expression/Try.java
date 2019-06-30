package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
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

	public Result compile(Variables variables)
	{
		Result result = this.expression.compile(variables);
		Type type = this.expression.getType(variables);

		/*if (type.isPrimitive())
		{
			methodNode.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				type.toFullyQualifiedType(),
				"valueOf",
				"(" + type.toJVMType() + ")L" + type.toFullyQualifiedType() + ";",
				false);
		}*/

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}
}
