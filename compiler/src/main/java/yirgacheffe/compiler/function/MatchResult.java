package yirgacheffe.compiler.function;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.lang.Array;

public class MatchResult
{
	private Callable function;

	private Array<MismatchedTypes> mismatchedParameters;

	private boolean ambiguous;

	private Coordinate coordinate;

	public MatchResult(Coordinate coordinate, String name, boolean ambiguous)
	{
		this.coordinate = coordinate;
		this.function = new NullFunction(name);
		this.mismatchedParameters = new Array<>();
		this.ambiguous = ambiguous;
	}

	public MatchResult(
		Coordinate coordinate,
		Callable function,
		Array<MismatchedTypes> mismatchedParameters)
	{
		this.coordinate = coordinate;
		this.function = function;
		this.mismatchedParameters = mismatchedParameters;
	}

	public boolean isSuccessful()
	{
		return !(this.function instanceof NullFunction);
	}

	public Callable getFunction()
	{
		return this.function;
	}

	public Array<MismatchedTypes> getMismatchedParameters()
	{
		return this.mismatchedParameters;
	}

	public boolean isAmbiguous()
	{
		return this.ambiguous;
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}

	public String getName()
	{
		return this.function.getName();
	}
}
