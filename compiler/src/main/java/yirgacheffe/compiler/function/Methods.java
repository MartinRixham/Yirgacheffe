package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Method;

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
		Method[] methods;

		if (this.owner.toFullyQualifiedType().equals(this.caller))
		{
			methods = this.owner.reflectionClass().getDeclaredMethods();
		}
		else
		{
			methods = this.owner.reflectionClass().getMethods();
		}

		Array<Callable> namedMethods = new Array<>();

		for (Method method: methods)
		{
			if (method.getName().equals(name))
			{
				namedMethods.push(new Function(this.owner, method));
			}
		}

		return namedMethods;
	}
}
