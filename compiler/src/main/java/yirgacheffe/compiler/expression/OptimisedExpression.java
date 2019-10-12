package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class OptimisedExpression implements Expression, Parameterisable
{
	private Expression expression;

	public OptimisedExpression(Expression expression)
	{
		this.expression = expression;
	}

	public Type getType(Variables variables)
	{
		return this.expression.getType(variables);
	}

	public Result compile(Variables variables)
	{
		Result originalResult = this.expression.compile(variables);
		Result result = new Result();

		for (AbstractInsnNode instruction: originalResult.getInstructions())
		{
			result = result.add(instruction);
		}

		for (TryCatchBlockNode tryCatchBlock: originalResult.getTryCatchBlocks())
		{
			result = result.add(tryCatchBlock);
		}

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		Result originalResult =
			this.expression.compileCondition(variables, trueLabel, falseLabel);

		Result result = new Result();

		for (AbstractInsnNode instruction: originalResult.getInstructions())
		{
			result = result.add(instruction);
		}

		for (TryCatchBlockNode tryCatchBlock: originalResult.getTryCatchBlocks())
		{
			result = result.add(tryCatchBlock);
		}

		return result;
	}

	public boolean isCondition(Variables variables)
	{
		return this.expression.isCondition(variables);
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}

	public Result compileArguments(Variables variables)
	{
		return ((Parameterisable) this.expression).compileArguments(variables);
	}

	public Array<Type> getParameters(Variables variables)
	{
		return ((Parameterisable) this.expression).getParameters(variables);
	}

	@Override
	public boolean equals(Object other)
	{
		return this.expression.equals(other);
	}

	@Override
	public int hashCode()
	{
		return this.expression.hashCode();
	}
}
