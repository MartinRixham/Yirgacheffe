package yirgacheffe.compiler;

public class Type
{
	private String name;

	public Type(String name)
	{
		this.name = name;
	}

	public String getJVMType()
	{
		if (this.name.equals("num"))
		{
			return "D";
		}
		else
		{
			return "Ljava/lang/" + this.name + ";";
		}
	}
}
