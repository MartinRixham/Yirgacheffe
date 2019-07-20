package yirgacheffe.compiler.variables;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.Map;

public class OptimisedVariables implements Variables
{
	private Map<String, Variable> variables;

	public OptimisedVariables(Variables variables)
	{
		this.variables = variables.getVariables();
	}

	public Map<String, Variable> getVariables()
	{
		return new HashMap<>();
	}

	public void setVariables(Map<String, Variable> variables)
	{
	}

	public void declare(String name, Type type)
	{
		Variable variable = new Variable(-1, type);

		this.variables.put(name, variable);
	}

	public void read(VariableRead variableRead)
	{
	}

	public void write(VariableWrite variableWrite)
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

	public Array<Error> getErrors()
	{
		return new Array<>();
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
