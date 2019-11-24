package yirgacheffe.compiler.type;

import yirgacheffe.compiler.Result;

public class MismatchedTypes
{
	private String from;

	private String to;

	public MismatchedTypes(String from, String to)
	{
		this.from = from;
		this.to = to;
	}

	public String from()
	{
		return this.from;
	}

	public String to()
	{
		return this.to;
	}
}
