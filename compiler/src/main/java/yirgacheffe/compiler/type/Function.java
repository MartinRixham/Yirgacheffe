package yirgacheffe.compiler.type;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;

public class Function
{
	private Executable executable;

	public Function(Executable executable)
	{
		this.executable = executable;
	}

	public Type getReturnType(Type owner)
	{
		Method method = (Method) this.executable;
		java.lang.reflect.Type returned = method.getGenericReturnType();

		Class<?> returnClass;

		if (returned instanceof Class)
		{
			returnClass = (Class) returned;
		}
		else
		{
			ParameterisedType parameterisedOwner = (ParameterisedType) owner;

			returnClass =
				parameterisedOwner.getTypeParameterClass(returned.getTypeName());
		}

		if (returnClass.isArray())
		{
			return new ArrayType(returnClass.getName());
		}
		else if (returnClass.isPrimitive())
		{
			return PrimitiveType.valueOf(returnClass.getName().toUpperCase());
		}
		else
		{
			return new ReferenceType(returnClass);
		}
	}
}
