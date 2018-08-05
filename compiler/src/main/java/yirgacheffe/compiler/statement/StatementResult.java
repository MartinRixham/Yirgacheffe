package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.Map;

public class StatementResult
{
	private int nextVariableIndex = 1;

	private Map<String, Variable> declaredVariables = new HashMap<>();

	private Array<VariableRead> variableReads = new Array<>();

	private Array<VariableWrite> variableWrites = new Array<>();

	private Array<Error> errors = new Array<>();

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

	public void error(Error error)
	{
		this.errors.push(error);
	}

	public Array<Error> getErrors()
	{
		for (VariableRead read: this.variableReads)
		{
			String message = "Unknown local variable '" + read.getName() + "'.";

			this.errors.push(new Error(read.getCoordinate(), message));
		}

		for (VariableWrite write: this.variableWrites)
		{
			String message =
				"Assignment to uninitialised variable '" + write.getName() + "'.";

			this.errors.push(new Error(write.getCoordinate(), message));
		}

		return this.errors;
	}
}
