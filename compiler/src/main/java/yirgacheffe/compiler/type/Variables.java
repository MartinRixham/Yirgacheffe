package yirgacheffe.compiler.type;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class Variables
{
	private int nextVariableIndex = 1;

	private Map<String, Variable> declaredVariables = new HashMap<>();

	private Set<String> declaredIntegers = new HashSet<>();

	private Array<VariableRead> variableReads = new Array<>();

	private Array<VariableWrite> variableWrites = new Array<>();

	private Map<Expression, Expression> optimisedVariables = new IdentityHashMap<>();

	private Map<String, Object> constants;

	public Variables(Map<String, Object> constants)
	{
		this.constants = constants;
	}

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

	public void declareInteger(String name)
	{
		this.declaredIntegers.add(name);
	}

	public void read(VariableRead variableRead)
	{
		String name = variableRead.getName();

		if (!this.declaredVariables.containsKey(name) &&
			!this.constants.containsKey(name))
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
			if (this.declaredIntegers.contains(name))
			{
				int index = this.declaredVariables.get(name).getIndex();

				return new Variable(index, PrimitiveType.INT);
			}
			else
			{
				return this.declaredVariables.get(name);
			}
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
