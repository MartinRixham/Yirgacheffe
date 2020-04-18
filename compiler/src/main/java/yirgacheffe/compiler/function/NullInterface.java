package yirgacheffe.compiler.function;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NullInterface implements Interface
{
	public Set<Function> getConstructors()
	{
		return new HashSet<>(Collections.singletonList(new NullFunction()));
	}

	public Set<Function> getPublicConstructors()
	{
		return new HashSet<>(Collections.singletonList(new NullFunction()));
	}

	public Set<Function> getMethods()
	{
		return new HashSet<>(Collections.singletonList(new NullFunction()));
	}

	public Set<Function> getPublicMethods()
	{
		return new HashSet<>(Collections.singletonList(new NullFunction()));
	}
}
