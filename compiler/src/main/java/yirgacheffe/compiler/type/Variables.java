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

	private Map<String, Variable> declaredVariables = new HashMap<>();

	private Array<VariableRead> variableReads = new Array<>();

	private Array<VariableWrite> variableWrites = new Array<>();

	private Map<VariableRead, Expression> optimisedVariables = new IdentityHashMap<>();

	public Map<String, Variable> getDeclaredVariables()
	{
		return new HashMap<>(this.declaredVariables);
	}

	public void setDeclaredVariables(Map<String, Variable> declaredVariables)
	{
		this.declaredVariables = declaredVariables;
	}

	public void declare(String name, Type type)
	{
		Variable variable = new Variable(this.nextVariableIndex, type);

		this.declaredVariables.put(name, variable);
		this.nextVariableIndex += type.width();
	}

	public void read(VariableRead variableRead)
	{
		if (!this.declaredVariables.containsKey(variableRead.getName()))
		{
			this.variableReads.push(variableRead);
		}
	}

	public void write(VariableWrite variableWrite)
	{
		if (!this.declaredVariables.containsKey(variableWrite.getName()))
		{
			this.variableWrites.push(variableWrite);
		}
	}

	public Variable getVariable(String name)
	{
		if (this.declaredVariables.containsKey(name))
		{
			return this.declaredVariables.get(name);
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

	public void optimise(VariableRead variableRead, Expression expression)
	{
		this.optimisedVariables.put(variableRead, expression);
	}

	public boolean canOptimise(VariableRead variableRead)
	{
		return this.optimisedVariables.keySet().contains(variableRead);
	}

	public Expression getOptimisedExpression(VariableRead variableRead)
	{
		return this.optimisedVariables.get(variableRead);
	}
}
