package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class VariableRead implements Expression
{
	private Coordinate coordinate;

	private String name;

	public VariableRead(Coordinate coordinate, String name)
	{
		this.coordinate = coordinate;
		this.name = name;
	}

	public Type getType(Variables variables)
	{
		return variables.getVariable(this.name).getType();
	}

	public Result compile(Variables variables)
	{
		Result result = new Result();

		if (variables.hasConstant(this.name))
		{
			result = result.add(new LdcInsnNode(variables.getConstant(this.name)));

			variables.stackPush(this.getType(variables));
		}
		else
		{
			if (variables.canOptimise(this))
			{
				Expression optimisedExpression = variables.getOptimisedExpression(this);

				result = result.concat(optimisedExpression.compile(variables));
			}
			else
			{
				variables.read(this);

				Variable variable = variables.getVariable(this.name);
				int loadInstruction = variable.getType().getLoadInstruction();
				int index = variable.getIndex();

				result = result.add(new VarInsnNode(loadInstruction, index));

				variables.stackPush(this.getType(variables));
			}
		}

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		if (variables.canOptimise(this))
		{
			Expression expression = variables.getOptimisedExpression(this);

			return expression.compileCondition(variables, trueLabel, falseLabel);
		}
		else
		{
			return this.compile(variables);
		}
	}

	public boolean isCondition(Variables variables)
	{
		if (variables.canOptimise(this))
		{
			Expression expression = variables.getOptimisedExpression(this);

			return expression.isCondition(variables);
		}
		else
		{
			return false;
		}
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>(this);
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}

	public String getName()
	{
		return this.name;
	}

	@Override
	public boolean equals(Object other)
	{
		return other.equals(this.name);
	}

	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}
}
