package yirgacheffe.compiler.function;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.lang.Array;

public class MatchResult
{
	private Callable function;

	private Array<MismatchedTypes> mismatchedParameters;

	private boolean ambiguous;

	private Coordinate coordinate;

	private boolean isConstructor;

	public MatchResult(
		Coordinate coordinate,
		String name,
		boolean ambiguous,
		boolean isConstructor)
	{
		this.coordinate = coordinate;
		this.function = new NullFunction(name);
		this.mismatchedParameters = new Array<>();
		this.ambiguous = ambiguous;
		this.isConstructor = isConstructor;
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

	public Callable getFunction()
	{
		return this.function;
	}

	public Result getResult()
	{
		Result result = new Result();

		if (this.function instanceof NullFunction)
		{
			String function = this.isConstructor ? "Constructor" : "Method";

			if (this.ambiguous)
			{
				String message =
					"Ambiguous call to " + function.toLowerCase() + " " +
					this.function.getName() + ".";

				result = result.add(new Error(this.coordinate, message));
			}
			else
			{
				String message =
					function + " " + this.function.getName() + " not found.";

				result = result.add(new Error(this.coordinate, message));
			}
		}

		for (MismatchedTypes types: this.mismatchedParameters)
		{
			String message =
				"Argument of type " + types.from() + " cannot be assigned to " +
				"generic parameter of type " + types.to() + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		return result;
	}
}
