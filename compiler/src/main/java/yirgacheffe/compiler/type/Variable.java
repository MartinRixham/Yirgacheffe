package yirgacheffe.compiler.type;

public class Variable
{
	private int index;

	private Type type;

	public Variable(int index, Type type)
	{
		this.index = index;
		this.type = type;
	}

	public int getIndex()
	{
		return  this.index;
	}

	public Type getType()
	{
		return this.type;
	}
}
