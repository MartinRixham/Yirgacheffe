package yirgacheffe.compiler.type;

public class Variable
{
	private int index;

	private String type;

	public Variable(int index, String type)
	{
		this.index = index;
		this.type = type;
	}

	public int getIndex()
	{
		return  this.index;
	}

	public String getType()
	{
		return this.type;
	}
}
