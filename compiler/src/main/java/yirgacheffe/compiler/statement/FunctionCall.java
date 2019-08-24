package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.InvokeThis;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
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

	public Result compile(Variables variables, Signature caller)
	{
		Type type = this.expression.getType(variables);

		Result result = this.expression.compile(variables);

		int width = type.width();

		if (width == 1)
		{
			result = result.add(new InsnNode(Opcodes.POP));
		}
		else if (width == 2)
		{
			result = result.add(new InsnNode(Opcodes.POP2));
		}

		variables.stackPop();

		return result;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public Array<String> getFieldAssignments()
	{
		if (this.expression instanceof InvokeThis)
		{
			return new Array<>("this");
		}
		else
		{
			return new Array<>();
		}
	}

	public Expression getExpression()
	{
		return this.expression;
	}

	@Override
	public boolean equals(Object object)
	{
		return this.expression.equals(object);
	}

	@Override
	public int hashCode()
	{
		return this.expression.hashCode();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
