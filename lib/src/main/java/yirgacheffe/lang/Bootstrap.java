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
					boolean.class,
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

	public static CallSite bootstrapPublic(
		MethodHandles.Lookup lookup,
		String name,
		MethodType type)
	{
		MethodHandle dispatcher =
			MethodHandles.insertArguments(DISPATCHER, 0, lookup, name, false)
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

	public static CallSite bootstrapPrivate(
			MethodHandles.Lookup lookup,
			String name,
			MethodType type)
	{
		MethodHandle dispatcher =
				MethodHandles.insertArguments(DISPATCHER, 0, lookup, name, true)
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
		boolean isPrivate,
		Object receiver,
		Object[] arguments) throws IllegalAccessException
	{

		Method[] methods;

		try
		{
			methods = receiver.getClass().getMethods();
		}
		catch (NullPointerException e)
		{
			// Remove top line of stack trace.
			StackTraceElement[] cleanedUpStackTrace =
				new StackTraceElement[e.getStackTrace().length - 1];

			java.lang.System.arraycopy(
				e.getStackTrace(),
				1,
				cleanedUpStackTrace,
				0,
				cleanedUpStackTrace.length);

			e.setStackTrace(cleanedUpStackTrace);

			throw e;
		}

		if (isPrivate)
		{
			Method[] declaredMethods = receiver.getClass().getDeclaredMethods();
			Method[] both = new Method[methods.length + declaredMethods.length];

			java.lang.System.arraycopy(methods, 0, both, 0, methods.length);
			java.lang.System.arraycopy(
					declaredMethods, 0, both, methods.length, declaredMethods.length);

			methods = both;
		}

		Method matchedMethod = null;
		int bestMatches = -1;

		for (int i = 0; i < methods.length; i++)
		{
			Class<?>[] parameterTypes = methods[i].getParameterTypes();

			if (methods[i].getName().equals(methodName) &&
				arguments.length == parameterTypes.length)
			{
				boolean matches = true;
				int exactMatches = 0;

				for (int j = 0; j < arguments.length; j++)
				{
					if (!typesMatch(parameterTypes[j], arguments[j].getClass()))
					{
						matches = false;
						break;
					}
					else if (parameterTypes[j].getName()
						.equals(arguments[j].getClass().getName()))
					{
						exactMatches++;
					}
				}

				if (matches && exactMatches > bestMatches)
				{
					bestMatches = exactMatches;
					matchedMethod = methods[i];
				}
			}
		}

		return lookup.unreflect(matchedMethod);
	}

	private static boolean typesMatch(Class<?> parameter, Class<?> argument)
	{
		if (parameter.isAssignableFrom(argument))
		{
			return true;
		}
		else if (parameter.isPrimitive())
		{
			return argument.getSimpleName().substring(0, 2).toLowerCase()
				.equals(parameter.getName().substring(0, 2));
		}
		else
		{
			return false;
		}
	}
}
