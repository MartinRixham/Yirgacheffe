package yirgacheffe.compiler.function;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class FailedMatchResult implements MatchResult
{
	private Coordinate coordinate;

	private String name;

	public FailedMatchResult(
		Coordinate coordinate,
		String name)
	{
		this.coordinate = coordinate;
		this.name = name;
	}

	public MatchResult betterOf(MatchResult other)
	{
		return other;
	}

	public int score()
	{
		return -1;
	}

	public Result compileArguments(Variables variables)
	{
		String message = "Invoked " + this.name + " not found.";

		return new Result().add(new Error(this.coordinate, message));
	}

	public String getName()
	{
		return "";
	}

	public Array<Type> getParameterTypes()
	{
		return new Array<>();
	}

	public Type getReturnType()
	{
		return new NullType();
	}
}
