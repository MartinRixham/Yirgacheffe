package yirgacheffe.compiler.type;

import yirgacheffe.compiler.MatchResult;

import java.util.ArrayList;
import java.util.List;

public class Executables
{
	private List<Function> executables;

	public Executables(List<Function> executables)
	{
		this.executables = executables;
	}

	public MatchResult getMatchingExecutable(ArgumentClasses argumentClasses)
	{
		List<Function> matched = new ArrayList<>();
		int highestExactMatches = 0;
		String argumentDescriptor = "()";

		for (Function executable: this.executables)
		{
			List<Type> parameterTypes = executable.getParameterTypes();
			int exactMatches = argumentClasses.matches(parameterTypes);

			if (exactMatches > highestExactMatches)
			{
				matched = new ArrayList<>();
				highestExactMatches = exactMatches;
			}

			if (exactMatches == highestExactMatches)
			{
				matched.add(executable);
				argumentDescriptor = this.getDescriptor(parameterTypes);
			}
		}

		if (matched.size() == 1)
		{
			List<MismatchedTypes> mismatchedParameters =
				matched.get(0).checkTypeParameter(argumentClasses);

			return new MatchResult(
				matched.get(0),
				argumentDescriptor,
				mismatchedParameters);
		}
		else
		{
			return new MatchResult();
		}
	}

	private String getDescriptor(List<Type> parameterTypes)
	{
		StringBuilder descriptor = new StringBuilder();

		descriptor.append("(");

		for (Type parameterType: parameterTypes)
		{
			descriptor.append(parameterType.toJVMType());
		}

		descriptor.append(")");

		return descriptor.toString();
	}
}
