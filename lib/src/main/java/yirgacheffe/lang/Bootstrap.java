package yirgacheffe.lang;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class Bootstrap
{
	static final MethodHandle DISPATCHER;

	static
	{
		try
		{
			MethodType dispatcherType =
				MethodType.methodType(
					MethodHandle.class,
					MethodHandles.Lookup.class,
					MethodHandle.class,
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
		MethodHandle target;

		try
		{
			target = lookup.findVirtual(
				type.parameterType(0),
				name,
				type.dropParameterTypes(0, 1));
		}
		catch (NoSuchMethodException | IllegalAccessException ex)
		{
			throw new BootstrapMethodError(ex);
		}

		MethodHandle dispatcher =
			MethodHandles.insertArguments(DISPATCHER, 0, lookup, target, name)
				.asCollector(Object[].class, type.parameterCount() - 1);

		for (int i = 0; i < type.parameterCount(); i++)
		{
			dispatcher =
				dispatcher.asType(
					dispatcher.type().changeParameterType(i, type.parameterType(i)));
		}

		target = MethodHandles.foldArguments(MethodHandles.invoker(type), dispatcher);

		return new ConstantCallSite(target);
	}

	public static MethodHandle runtimeDispatcher(
		MethodHandles.Lookup lookup,
		MethodHandle invokeVirtualTarget,
		String methodName,
		Object receiver,
		Object... arguments)
	{
		MethodType methodType = invokeVirtualTarget.type().dropParameterTypes(0, 1);

		for (int i = 0; i < arguments.length; i++)
		{
			methodType = methodType.changeParameterType(i, arguments[i].getClass());
		}

		try
		{
			return lookup.findVirtual(receiver.getClass(), methodName, methodType);
		}
		catch (NoSuchMethodException | IllegalAccessException ex)
		{
			return invokeVirtualTarget;
		}
	}
}
