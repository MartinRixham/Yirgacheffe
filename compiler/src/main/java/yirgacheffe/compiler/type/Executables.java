package yirgacheffe.compiler.type;

import yirgacheffe.compiler.MatchResult;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

public class Executables<T extends Executable>
{
	private List<T> executables;

	public Executables(List<T> executables)
	{
		this.executables = executables;
	}

	public MatchResult<T> getMatchingExecutable(ArgumentClasses argumentClasses)
	{
		List<T> matched = new ArrayList<>();
		int highestExactMatches = 0;
		String argumentDescriptor = "()";

		for (T executable: this.executables)
		{
			Type[] parameterTypes = this.getTypes(executable.getParameterTypes());
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
			return new MatchResult<>(matched.get(0), argumentDescriptor);
		}
		else
		{
			return new MatchResult<>();
		}
	}

	private String getDescriptor(Type[] parameterTypes)
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

	private Type[] getTypes(Class<?>[] classes)
	{
		Type[] types = new Type[classes.length];

		for (int i = 0; i < classes.length; i++)
		{
			if (classes[i].isArray())
			{
				types[i] = new ArrayType(classes[i].getName());
			}
			else if (classes[i].isPrimitive())
			{
				types[i] = PrimitiveType.valueOf(classes[i].getName().toUpperCase());
			}
			else
			{
				types[i] = new ReferenceType(classes[i]);
			}
		}

		return types;
	}
}
