package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.Type;

import java.util.ArrayList;
import java.util.List;

public class Functions
{
	private List<Callable> functions;

	public Functions(List<Callable> functions)
	{
		this.functions = functions;
	}

	public MatchResult getMatchingExecutable(ArgumentClasses argumentClasses)
	{
		List<Callable> matched = new ArrayList<>();
		int highestExactMatches = 0;

		for (Callable function: this.functions)
		{
			List<Type> parameterTypes = function.getParameterTypes();
			int exactMatches = argumentClasses.matches(parameterTypes);

			if (exactMatches > highestExactMatches)
			{
				matched = new ArrayList<>();
				highestExactMatches = exactMatches;
			}

			if (exactMatches == highestExactMatches)
			{
				matched.add(function);
			}
		}

		if (matched.size() == 1)
		{
			List<MismatchedTypes> mismatchedParameters =
				matched.get(0).checkTypeParameters(argumentClasses);

			return new MatchResult(matched.get(0), mismatchedParameters);
		}
		else
		{
			return new MatchResult(!matched.isEmpty());
		}
	}
}
