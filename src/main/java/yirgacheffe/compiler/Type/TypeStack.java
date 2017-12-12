package yirgacheffe.compiler.Type;

import java.util.Stack;

public class TypeStack
{
	private Stack<String> stack = new Stack<>();

	private int maxSize = 0;

	public void push(String type)
	{
		this.stack.push(type);

		this.maxSize = Math.max(this.stack.size(), this.maxSize);
	}

	public String pop()
	{
		return this.stack.pop();
	}

	public int reset()
	{
		int maxSize = this.maxSize;

		this.maxSize = 0;

		return maxSize;
	}
}
