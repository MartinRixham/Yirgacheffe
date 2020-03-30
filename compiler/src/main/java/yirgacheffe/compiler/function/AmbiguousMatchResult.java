package yirgacheffe.compiler.function;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class AmbiguousMatchResult implements MatchResult
{
	private Coordinate coordinate;

	private String name;

	private int score;

	public AmbiguousMatchResult(
		Coordinate coordinate,
		String name,
		int score)
	{
		this.coordinate = coordinate;
		this.name = name;
		this.score = score;
	}

	public MatchResult betterOf(MatchResult other)
	{
		if (this.score < other.score())
		{
			return other;
		}
		else
		{
			return this;
		}
	}

	public int score()
	{
		return this.score;
	}

	public Result compileArguments(Variables variables)
	{
		String message = "Ambiguous call to " + this.name + ".";

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
