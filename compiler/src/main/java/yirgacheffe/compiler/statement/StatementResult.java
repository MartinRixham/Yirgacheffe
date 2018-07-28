package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.MatchResult;
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

	private Array<MatchResult> matchMethodResults = new Array<>();

	private Array<MatchResult> matchConstructorResults = new Array<>();

	private Array<Error> errors = new Array<>();

	public Array<VariableRead> getVariableReads()
	{
		return this.variableReads;
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return this.variableWrites;
	}

	public Map<String, Variable> getDeclaredVariables()
	{
		return new HashMap<>(this.declaredVariables);
	}

	public void setDeclaredVariables(Map<String, Variable> declaredVariables)
	{
		this.declaredVariables = declaredVariables;
	}

	public Array<MatchResult> getMatchMethodResults()
	{
		return this.matchMethodResults;
	}

	public Array<MatchResult> getMatchConstructorResults()
	{
		return this.matchConstructorResults;
	}

	public Array<Error> getErrors()
	{
		return this.errors;
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

	public void matchMethod(MatchResult matchResult)
	{
		this.matchMethodResults.push(matchResult);
	}

	public void matchConstructor(MatchResult matchResult)
	{
		this.matchConstructorResults.push(matchResult);
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
}
