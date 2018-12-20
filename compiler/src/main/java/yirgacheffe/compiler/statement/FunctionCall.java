package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class FunctionCall implements Statement
{
	private Expression expression;

	public FunctionCall(Expression expression)
	{
		this.expression = expression;
	}

	public boolean returns()
	{
		return false;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Type type = this.expression.getType(variables);

		Array<Error> errors = this.expression.compile(methodVisitor, variables);

		int width = type.width();

		if (width == 1)
		{
			methodVisitor.visitInsn(Opcodes.POP);
		}
		else if (width == 2)
		{
			methodVisitor.visitInsn(Opcodes.POP2);
		}

		return errors;
	}

	public Expression getFirstOperand()
	{
		return this.expression.getFirstOperand();
	}

	@Override
	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}

	@Override
	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}
}
