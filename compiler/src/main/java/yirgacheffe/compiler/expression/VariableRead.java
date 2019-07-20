package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class VariableRead implements Expression
{
	private String name;

	private Coordinate coordinate;

	public VariableRead(Coordinate coordinate, String name)
	{
		this.name = name;
		this.coordinate = coordinate;
	}

	public Type getType(Variables variables)
	{
		return variables.getVariable(this.name).getType();
	}

	public Result compile(Variables variables)
	{
		if (variables.hasConstant(this.name))
		{
			return new Result().add(new LdcInsnNode(variables.getConstant(this.name)));
		}
		else
		{
			if (variables.canOptimise(this))
			{
				return variables.getOptimisedExpression(this).compile(variables);
			}
			else
			{
				variables.read(this);
				variables.stackPush(this.getType(variables));

				Variable variable = variables.getVariable(this.name);
				int loadInstruction = variable.getType().getLoadInstruction();
				int index = variable.getIndex();

				return new Result().add(new VarInsnNode(loadInstruction, index));
			}
		}
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
		if (other instanceof String)
		{
			return this.name.equals(other);
		}

		if (other instanceof VariableRead || other instanceof VariableWrite)
		{
			return other.equals(this.name);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>(this);
	}
}
