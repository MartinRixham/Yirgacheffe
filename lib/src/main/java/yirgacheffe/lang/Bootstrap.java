package yirgacheffe.lang;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class Bootstrap
{
	private static final int THOUSAND = 1024;

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

	private static Map<Object, String[]> objectSignature = new IdentityHashMap<>();

	private Bootstrap()
	{
	}

	public static void cacheObjectSignature(Object object, String signature)
	{
		if (!objectSignature.containsKey(object))
		{
			String[] parameterTypes = signature.split(",");

			objectSignature.put(object, parameterTypes);
		}
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
		Object[] arguments) throws Throwable
	{
		if (receiver == null)
		{
			NullPointerException exception = new NullPointerException();

			// Remove top line of stack trace.
			StackTraceElement[] stackTrace =
				new StackTraceElement[exception.getStackTrace().length - 1];

			java.lang.System.arraycopy(
				exception.getStackTrace(), 1, stackTrace, 0, stackTrace.length);

			exception.setStackTrace(stackTrace);

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
			Type[] parameterTypes = method.getGenericParameterTypes();

			if (method.getName().equals(methodName) &&
				arguments.length == parameterTypes.length)
			{
				int matching = evaluateMatching(parameterTypes, arguments);

				if (matching > bestMatching)
				{
					bestMatching = matching;
					matchedMethod = method;
				}
			}
		}

		if (matchedMethod == null)
		{
			if (receiver instanceof Throwable)
			{
				throw ((Throwable) receiver);
			}

			for (Object argument: arguments)
			{
				if (argument instanceof Throwable)
				{
					throw ((Throwable) argument);
				}
			}
		}

		MethodHandle methodHandle = lookup.unreflect(matchedMethod);

		methodHandles.put(methodString, methodHandle);

		return methodHandle;
	}

	private static int evaluateMatching(Type[] parameters, Object[] arguments)
	{
		int matching = 0;

		for (int i = 0; i < arguments.length; i++)
		{
			Type parameter = parameters[i];
			Object argumentReference = arguments[i];
			Class<?> argument = argumentReference.getClass();

			if (parameter instanceof ParameterizedType)
			{
				ParameterizedType parameterizedType = (ParameterizedType) parameter;
				Type[] typeArguments = parameterizedType.getActualTypeArguments();

				String[] typeNames = new String[typeArguments.length];
				boolean variableType = false;

				for (int j = 0; j < typeArguments.length; j++)
				{
					if (typeArguments[j] instanceof TypeVariable)
					{
						variableType = true;
					}

					typeNames[j] = typeArguments[j].getTypeName();
				}

				if (variableType ||
					parameterizedType.getRawType().getTypeName()
						.equals(argument.getName()) &&
					Arrays.equals(typeNames, objectSignature.get(argumentReference)))
				{
					matching += THOUSAND;
				}
				else
				{
					return -1;
				}
			}
			else if (parameter instanceof TypeVariable)
			{
				matching += THOUSAND;
			}
			else if (parameter instanceof Class &&
				((Class<?>) parameter).isAssignableFrom(argument))
			{
				if (parameter.getTypeName().equals(argument.getName()))
				{
					matching += THOUSAND;
				}
			}
			else if (primitiveTypes.containsKey(argument))
			{
				Class<?> primitiveArgument = primitiveTypes.get(argument);

				if (parameter.equals(primitiveArgument))
				{
					matching += THOUSAND;
				}
				else if (numberTypes.contains(parameter) &&
					numberTypes.contains(primitiveArgument))
				{
					matching += 1;
				}
				else
				{
					return -1;
				}
			}
			else
			{
				return -1;
			}
		}

		return matching;
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
