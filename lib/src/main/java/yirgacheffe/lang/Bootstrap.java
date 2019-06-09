package yirgacheffe.lang;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Bootstrap
{
	private static final int THOUSAND = 1024;

	private static final int MILLION = 1048576;

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

	private static Map<String, MethodHandle> methodHandles = new HashMap<>();

	private static Map<Class<?>, Class<?>> primitiveTypes = new HashMap<>();

	static
	{
		primitiveTypes.put(Boolean.class, boolean.class);
		primitiveTypes.put(Character.class, char.class);
		primitiveTypes.put(Integer.class, int.class);
		primitiveTypes.put(Long.class, long.class);
		primitiveTypes.put(Double.class, double.class);
	}

	private static Set<Class<?>> numberTypes = new HashSet<>();

	static
	{
		numberTypes.add(int.class);
		numberTypes.add(long.class);
		numberTypes.add(double.class);
	}

	private Bootstrap()
	{
	}

	public static CallSite bootstrapPublic(
		MethodHandles.Lookup lookup,
		String name,
		MethodType type)
	{
		return bootstrap(lookup, name, type, false);
	}

	public static CallSite bootstrapPrivate(
			MethodHandles.Lookup lookup,
			String name,
			MethodType type)
	{
		return bootstrap(lookup, name, type, true);
	}

	private static CallSite bootstrap(
		MethodHandles.Lookup lookup,
		String name,
		MethodType type,
		boolean isPrivate)

	{
		MethodHandle dispatcher =
			MethodHandles.insertArguments(DISPATCHER, 0, lookup, name, isPrivate)
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
		if (receiver == null)
		{
			NullPointerException exception = new NullPointerException();

			// Remove top line of stack trace.
			StackTraceElement[] cleanedUpStackTrace =
				new StackTraceElement[exception.getStackTrace().length - 1];

			java.lang.System.arraycopy(
				exception.getStackTrace(),
				1,
				cleanedUpStackTrace,
				0,
				cleanedUpStackTrace.length);

			exception.setStackTrace(cleanedUpStackTrace);

			throw exception;
		}

		String methodString = stringify(methodName, isPrivate, receiver, arguments);

		if (methodHandles.containsKey(methodString))
		{
			return methodHandles.get(methodString);
		}

		Method[] methods = receiver.getClass().getMethods();

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
		int bestMatching = -1;

		for (int i = 0; i < methods.length; i++)
		{
			Method method = methods[i];
			Class<?>[] parameterTypes = method.getParameterTypes();

			if (method.getName().equals(methodName) &&
				arguments.length == parameterTypes.length)
			{
				int matching = 0;

				for (int j = 0; j < arguments.length; j++)
				{
					Class<?> parameterClass = parameterTypes[j];
					Class<?> argumentClass = arguments[j].getClass();

					matching += evaluateMatching(parameterClass, argumentClass);

					if (matching < 0)
					{
						break;
					}
				}

				if (matching > bestMatching)
				{
					bestMatching = matching;
					matchedMethod = method;
				}
			}
		}

		MethodHandle methodHandle = lookup.unreflect(matchedMethod);

		methodHandles.put(methodString, methodHandle);

		return methodHandle;
	}

	private static int evaluateMatching(Class<?> parameter, Class<?> argument)
	{
		if (parameter.isAssignableFrom(argument))
		{
			if (parameter.getName().equals(argument.getName()))
			{
				return THOUSAND;
			}

			return 0;
		}
		else if (primitiveTypes.containsKey(argument))
		{
			Class<?> primitiveArgument = primitiveTypes.get(argument);

			if (parameter.equals(primitiveArgument))
			{
					return THOUSAND;
			}

			if (numberTypes.contains(parameter) &&
				numberTypes.contains(primitiveArgument))
			{
				return 1;
			}

			return -MILLION;
		}

		return -MILLION;
	}

	private static String stringify(
		String methodName,
		boolean isPrivate,
		Object receiver,
		Object[] arguments)
	{
		StringBuilder stringBuilder = new StringBuilder(methodName);
		stringBuilder.append(isPrivate);
		stringBuilder.append(receiver.getClass().getName());

		for (Object argument: arguments)
		{
			stringBuilder.append(argument.getClass().getName());
		}

		return stringBuilder.toString();
	}

	public static void clearCache()
	{
		methodHandles = new HashMap<>();
	}
}
