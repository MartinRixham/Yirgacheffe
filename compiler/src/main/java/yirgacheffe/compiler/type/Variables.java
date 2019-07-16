package yirgacheffe.compiler.type;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Variables
{
	private int nextVariableIndex = 1;

	private Map<String, Variable> variables = new HashMap<>();

	private Array<VariableRead> variableReads = new Array<>();

	private Array<VariableWrite> variableWrites = new Array<>();

	private Map<Expression, Expression> optimisedVariables = new IdentityHashMap<>();

	private Map<String, Object> constants;

	public Variables(Map<String, Object> constants)
	{
		this.constants = constants;
	}

	public Map<String, Variable> getVariables()
	{
		return new HashMap<>(this.variables);
	}

	public void setVariables(Map<String, Variable> variables)
	{
		this.variables = variables;
	}

	public void declare(String name, Type type)
	{
		Variable variable = new Variable(this.nextVariableIndex, type);

		this.variables.put(name, variable);
		this.nextVariableIndex += type.width();
	}

	public void declareOptimised(String name, Type type)
	{
		Variable variable = new Variable(-1, type);

		this.variables.put(name, variable);
	}

	public void read(VariableRead variableRead)
	{
		String name = variableRead.getName();

		if (!this.variables.containsKey(name) &&
			!this.constants.containsKey(name))
		{
			this.variableReads.push(variableRead);
		}
	}

	public void write(VariableWrite variableWrite)
	{
		String name = variableWrite.getName();

		if (this.variables.containsKey(name))
		{
			Variable variable = this.variables.get(name);

			if (variable.getType().isPrimitive())
			{
				Type type = variableWrite.getExpression().getType(this);

				this.variables.put(name, new Variable(variable.getIndex(), type));
			}
		}
		else
		{
			this.variableWrites.push(variableWrite);
		}
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
		Array<Error> errors = new Array<>();

		for (VariableRead read: this.variableReads)
		{
			String message = "Unknown local variable '" + read.getName() + "'.";

			errors.push(new Error(read.getCoordinate(), message));
		}

		for (VariableWrite write: this.variableWrites)
		{
			String message =
				"Assignment to uninitialised variable '" + write.getName() + "'.";

			errors.push(new Error(write.getCoordinate(), message));
		}

		return errors;
	}

	public void optimise(Expression variableRead, VariableWrite variableWrite)
	{
		this.optimisedVariables.put(variableRead, variableWrite.getExpression());
	}

	public boolean canOptimise(Expression variableRead)
	{
		return this.optimisedVariables.keySet().contains(variableRead);
	}

	public Expression getOptimisedExpression(Expression variableRead)
	{
		return this.optimisedVariables.get(variableRead);
	}

	public boolean hasConstant(String name)
	{
		return this.constants.containsKey(name);
	}

	public Object getConstant(String name)
	{
		return this.constants.get(name);
	}
}
