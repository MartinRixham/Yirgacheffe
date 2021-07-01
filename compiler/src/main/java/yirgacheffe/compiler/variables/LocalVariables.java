package yirgacheffe.compiler.variables;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.OptimisedExpression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.AttemptedType;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.lang.Array;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class LocalVariables implements Variables
{
	private int nextVariableIndex;

	private Map<String, Variable> variables = new HashMap<>();

	private Array<VariableRead> variableReads = new Array<>();

	private Array<VariableWrite> variableWrites = new Array<>();

	private Map<Expression, Expression> optimisedVariables = new IdentityHashMap<>();

	private Map<String, Object> constants;

	private Array<Type> stack = new Array<>();

	private Map<Delegate, Type> delegateTypes = new HashMap<>();

	public LocalVariables(int initialVariableIndex, Map<String, Object> constants)
	{
		this.nextVariableIndex = initialVariableIndex;
		this.constants = constants;
	}

	public Map<String, Variable> getVariables()
	{
		return this.variables;
	}

	public void setVariables(Map<String, Variable> variables)
	{
		this.variables = variables;
	}

	public void delegate(Delegate delegate, Type type)
	{
		this.delegateTypes.put(delegate, type);
	}

	public Map<Delegate, Type> getDelegateTypes()
	{
		return new HashMap<>(this.delegateTypes);
	}

	public void declare(String name, Type type)
	{
		Variable variable = new Variable(this.nextVariableIndex, type);

		this.variables.put(name, variable);
		this.nextVariableIndex += type.width();
	}

	public Variable redeclare(String name)
	{
		Variable variable = this.variables.get(name);

		if (variable.getIndex() < 0)
		{
			Type type = variable.getType();
			variable = new Variable(this.nextVariableIndex, type);

			this.nextVariableIndex += type.width();
			this.variables.put(name, variable);
		}

		return variable;
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
			Type type = variableWrite.getExpression().getType(this);

			if (type.isPrimitive() || type instanceof AttemptedType)
			{
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

	public void optimise(Expression variableRead, Expression writtenExpression)
	{
		this.optimisedVariables.put(variableRead, writtenExpression);
	}

	public boolean canOptimise(Expression variableRead)
	{
		return this.optimisedVariables.containsKey(variableRead);
	}

	public Expression getOptimisedExpression(Expression variableRead)
	{
		return new OptimisedExpression(this.optimisedVariables.get(variableRead));
	}

	public boolean hasConstant(String name)
	{
		return this.constants.containsKey(name);
	}

	public Object getConstant(String name)
	{
		return this.constants.get(name);
	}

	public int nextVariableIndex()
	{
		return this.nextVariableIndex;
	}

	public Array<Type> getStack()
	{
		return this.stack;
	}

	public void stackPush(Type type)
	{
		this.stack.push(type);
	}

	public void stackPop()
	{
		this.stack.pop();
	}
}
