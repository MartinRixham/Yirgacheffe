package yirgacheffe.lang;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public final class Bootstrap
{
	private static final MethodHandle DISPATCHER;

	static
	{
		try
		{
			MethodType dispatcherType =
				MethodType.methodType(
					MethodHandle.class,
					MethodHandles.Lookup.class,
					String.class,
					Object.class,
					Object[].class);

			DISPATCHER =
				MethodHandles.lookup().findStatic(
					Bootstrap.class,
					"runtimeDispatcher",
					dispatcherType);
		}
		catch (NoSuchMethodException | IllegalAccessException ex)
		{
			throw new ExceptionInInitializerError(ex);
		}
	}

	private Bootstrap()
	{
	}

	public static CallSite bootstrap(
		MethodHandles.Lookup lookup,
		String name,
		MethodType type)
	{
		MethodHandle dispatcher =
			MethodHandles.insertArguments(DISPATCHER, 0, lookup, name)
				.asCollector(Object[].class, type.parameterCount() - 1);

		MethodType dispatcherType = dispatcher.type();

		for (int i = 0; i < type.parameterCount(); i++)
		{
			dispatcherType = dispatcherType.changeParameterType(i, type.parameterType(i));
		}

		dispatcher = dispatcher.asType(dispatcherType);

		MethodHandle target =
			MethodHandles.foldArguments(MethodHandles.invoker(type), dispatcher);

		return new ConstantCallSite(target);
	}

	private static MethodHandle runtimeDispatcher(
		MethodHandles.Lookup lookup,
		String methodName,
		Object receiver,
		Object[] arguments) throws IllegalAccessException
	{
		Method[] methods = receiver.getClass().getMethods();
		Method[] namedMethods = new Method[methods.length];
		int namedMethodCount = 0;

		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i].getName().equals(methodName))
			{
				namedMethods[namedMethodCount++] = methods[i];
			}
		}

		Class<?>[] argumentTypes = new Class<?>[arguments.length];

		for (int i = 0; i < arguments.length; i++)
		{
			argumentTypes[i] = arguments[i].getClass();
		}

		Method matchedMethod = namedMethods[0];
		int bestMatches = -1;

		for (int i = 0; i < namedMethodCount; i++)
		{
			Class<?>[] parameterTypes = namedMethods[i].getParameterTypes();

			if (argumentTypes.length == parameterTypes.length)
			{
				boolean matches = true;
				int exactMatches = 0;

				for (int j = 0; j < argumentTypes.length; j++)
				{
					if (!parameterTypes[j].isAssignableFrom(argumentTypes[j]))
					{
						matches = false;
						break;
					}
					else if (parameterTypes[j].getName()
						.equals(argumentTypes[j].getName()))
					{
						exactMatches++;
					}
				}

				if (matches && exactMatches > bestMatches)
				{
					bestMatches = exactMatches;
					matchedMethod = namedMethods[i];
				}
			}
		}

		return lookup.unreflect(matchedMethod);
	}
}
