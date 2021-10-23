package yirgacheffe.compiler.variables;

import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.AttemptedType;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.Map;

public class PrerunVariables implements Variables
{
	private Variables localVariables;

	private Map<String, Variable> variables;

	public PrerunVariables(Variables variables)
	{
		if (variables instanceof PrerunVariables)
		{
			this.localVariables = ((PrerunVariables) variables).localVariables;
			this.variables = ((PrerunVariables) variables).variables;
		}
		else
		{
			this.localVariables = variables;
			this.variables = new HashMap<>();
		}
	}

	public Map<String, Variable> getVariables()
	{
		return new HashMap<>();
	}

	public void setVariables(Map<String, Variable> variables)
	{
	}

	public void delegate(Delegate delegate, Type type)
	{
	}

	public void declare(String name, Type type)
	{
		Variable variable = new Variable(-1, type);

		this.variables.put(name, variable);
	}

	public Variable redeclare(String name)
	{
		return this.variables.get(name);
	}

	public void read(VariableRead variableRead)
	{
	}

	public void write(VariableWrite variableWrite)
	{
		String name = variableWrite.getName();

		if (this.variables.containsKey(name))
		{
			Variable variable = this.variables.get(name);
			Type type = variableWrite.getExpression().getType(this);

			if (type.isPrimitive() || type instanceof AttemptedType)
			{
				this.variables.put(name, new Variable(variable.getIndex(), type));

				if (type.isAssignableTo(PrimitiveType.DOUBLE))
				{
					this.localVariables.setNumberType(name, type);
				}
			}
		}
	}

	public void setNumberType(String name, Type type)
	{
	}

	public Variable getVariable(String name)
	{
		if (this.variables.containsKey(name))
		{
			return this.variables.get(name);
		}
		else
		{
			return new Variable(0, new NullType());
		}
	}

	public void optimise(Expression variableRead, Expression writtenExpression)
	{
	}

	public boolean canOptimise(Expression variableRead)
	{
		return false;
	}

	public Expression getOptimisedExpression(Expression variableRead)
	{
		return new Nothing();
	}

	public boolean hasConstant(String name)
	{
		return false;
	}

	public Object getConstant(String name)
	{
		return null;
	}

	public int nextVariableIndex()
	{
		return 0;
	}

	public Array<Type> getStack()
	{
		return new Array<>();
	}

	public void stackPush(Type type)
	{
	}

	public void stackPop()
	{
	}
}
