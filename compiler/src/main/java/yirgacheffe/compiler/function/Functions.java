package yirgacheffe.compiler.function;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class Functions
{
	private Coordinate coordinate;

	private String name;

	private Array<Callable> functions;

	private boolean isConstructor;

	public Functions(
		Coordinate coordinate,
		String name,
		Array<Callable> functions,
		boolean isConstructor)
	{
		this.coordinate = coordinate;
		this.name = name;
		this.functions = functions;
		this.isConstructor = isConstructor;
	}

	public MatchResult getMatchingExecutable(Arguments arguments)
	{
		Array<Callable> matched = new Array<>();
		int highestExactMatches = 0;

		for (Callable function: this.functions)
		{
			Array<Type> parameterTypes = function.getParameterTypes();
			int exactMatches = arguments.matches(parameterTypes);

			if (exactMatches > highestExactMatches)
			{
				matched = new Array<>();
				highestExactMatches = exactMatches;
			}

			if (exactMatches == highestExactMatches)
			{
				matched.push(function);
			}
		}

		if (matched.length() == 1)
		{
			Array<MismatchedTypes> mismatchedParameters =
				matched.get(0).checkTypeParameters(arguments);

			return new MatchResult(this.coordinate, matched.get(0), mismatchedParameters);
		}
		else
		{
			return new MatchResult(
				this.coordinate,
				this.name + arguments,
				matched.length() > 0,
				this.isConstructor);
		}
	}
}
