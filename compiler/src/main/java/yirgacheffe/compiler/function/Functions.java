package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class Functions
{
	private Array<Callable> functions;

	public Functions(Array<Callable> functions)
	{
		this.functions = functions;
	}

	public MatchResult getMatchingExecutable(ArgumentClasses argumentClasses)
	{
		Array<Callable> matched = new Array<>();
		int highestExactMatches = 0;

		for (Callable function: this.functions)
		{
			Array<Type> parameterTypes = function.getParameterTypes();
			int exactMatches = argumentClasses.matches(parameterTypes);

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
				matched.get(0).checkTypeParameters(argumentClasses);

			return new MatchResult(matched.get(0), mismatchedParameters);
		}
		else
		{
			return new MatchResult(matched.length() > 0);
		}
	}
}
