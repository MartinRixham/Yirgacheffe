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

		if (returned instanceof Class)
		{
			return this.getType((Class) returned);
		}
		else
		{
			ParameterisedType parameterisedOwner = (ParameterisedType) owner;

			Class<?> returnClass =
				parameterisedOwner.getTypeParameterClass(returned.getTypeName());

			return new GenericType(this.getType(returnClass));
		}
	}

	private Type getType(Class<?> clazz)
	{
		if (clazz.isArray())
		{
			return new ArrayType(clazz.getName());
		}
		else if (clazz.isPrimitive())
		{
			return PrimitiveType.valueOf(clazz.getName().toUpperCase());
		}
		else
		{
			return new ReferenceType(clazz);
		}
	}
}
