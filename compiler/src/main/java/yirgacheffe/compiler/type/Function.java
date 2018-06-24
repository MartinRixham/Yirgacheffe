package yirgacheffe.compiler.type;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Function
{
	private Type owner;

	private Executable executable;

	public Function(Type owner, Executable executable)
	{
		this.owner = owner;
		this.executable = executable;
	}

	public Type getReturnType()
	{
		Method method = (Method) this.executable;
		java.lang.reflect.Type returned = method.getGenericReturnType();

		if (returned instanceof Class)
		{
			return this.getType((Class) returned);
		}
		else
		{
			ParameterisedType parameterisedOwner = (ParameterisedType) this.owner;

			Class<?> returnClass =
				parameterisedOwner.getTypeParameterClass(returned.getTypeName());

			return new GenericType(this.getType(returnClass));
		}
	}

	public List<Type> getParameterTypes()
	{
		Class<?>[] classes = this.executable.getParameterTypes();
		List<Type> types = new ArrayList<>();

		for (Class<?> clazz: classes)
		{
			types.add(this.getType(clazz));
		}

		return types;
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

	public List<MismatchedTypes> checkTypeParameter(ArgumentClasses argumentClasses)
	{
		List<MismatchedTypes> mismatchedParameters = new ArrayList<>();

		if (this.owner instanceof ParameterisedType)
		{
			ParameterisedType type = (ParameterisedType) this.owner;

			return argumentClasses.checkTypeParameters(
				this.executable.getGenericParameterTypes(), type);
		}
		else
		{
			return mismatchedParameters;
		}
	}
}
