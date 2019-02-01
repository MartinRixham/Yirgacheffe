package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Methods
{
	private Type owner;

	private String caller;

	public Methods(Type owner, String caller)
	{
		this.owner = owner;
		this.caller = caller;
	}

	public Array<Callable> getMethodsNamed(String name)
	{
		Set<Method> methodSet = new HashSet<>();

		methodSet.addAll(
			Arrays.asList(this.owner.reflectionClass().getMethods()));

		if (this.owner.toFullyQualifiedType().equals(this.caller))
		{
			methodSet.addAll(
				Arrays.asList(this.owner.reflectionClass().getDeclaredMethods()));
		}

		Array<Callable> namedMethods = new Array<>();

		for (Method method: methodSet)
		{
			if (method.getName().equals(name))
			{
				namedMethods.push(new Function(this.owner, method));
			}
		}

		return namedMethods;
	}
}
