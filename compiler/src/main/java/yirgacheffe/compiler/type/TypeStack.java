package yirgacheffe.compiler.type;

import java.util.Stack;

public class TypeStack
{
	private Stack<Type> stack = new Stack<>();

	private int maxSize = 0;

	private int currentInstantiations = 0;

	public void push(Type type)
	{
		this.stack.push(type);

		this.maxSize = Math.max(this.getSize(), this.maxSize);
	}

	private int getSize()
	{
		int size = 0;

		for (Type type: this.stack)
		{
			size += type.width();
		}

		return size + this.currentInstantiations;
	}

	public Type pop()
	{
		return this.stack.pop();
	}

	public int reset()
	{
		int maxSize = this.maxSize;

		this.maxSize = 0;

		return maxSize;
	}

	public boolean isEmpty()
	{
		return this.stack.size() == 0;
	}

	public void beginInstantiation()
	{
		this.currentInstantiations++;
	}

	public void endInstantiation()
	{
		this.currentInstantiations--;
	}
}
