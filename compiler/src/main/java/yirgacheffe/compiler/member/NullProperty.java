package yirgacheffe.compiler.member;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;

public class NullProperty implements Property
{
	private String name;

	public NullProperty(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public Result checkType(Coordinate coordinate, Type type)
	{
		String message = "Assignment to unknown field '" + this.name + "'.";

		return new Result().add(new Error(coordinate, message));
	}

	public boolean isStatic()
	{
		return false;
	}

	public Type getType()
	{
		return new NullType();
	}
}
