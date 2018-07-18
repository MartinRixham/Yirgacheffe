package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.expression.Variable;

import java.util.HashMap;
import java.util.Map;

public class Block
{
	private Block parent;

	private Map<String, Variable> localVariables = new HashMap<>();

	public Block()
	{
	}

	public Block(Block parent)
	{
		this.parent = parent;
	}

	public void declare(String name, Variable variable)
	{
		this.localVariables.put(name, variable);
	}

	public boolean isDeclared(String name)
	{
		boolean declared = this.localVariables.containsKey(name);

		if (declared || this.parent == null)
		{
			return declared;
		}
		else
		{
			return this.parent.isDeclared(name);
		}
	}

	public Variable getVariable(String name)
	{
		if (this.localVariables.containsKey(name))
		{
			return this.localVariables.get(name);
		}
		else
		{
			return this.parent.getVariable(name);
		}
	}

	public int size()
	{
		int size = 0;

		for (Variable variable: this.localVariables.values())
		{
			size += variable.getType().width();
		}

		if (this.parent == null)
		{
			return size;
		}
		else
		{
			return size + this.parent.size();
		}
	}

	public Block unwarap()
	{
		return this.parent;
	}
}
