package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.ArrayType;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Function implements Callable
{
	private Type owner;

	private Executable executable;

	public Function(Type owner, Executable executable)
	{
		this.owner = owner;
		this.executable = executable;
	}

	@Override
	public String getName()
	{
		return this.executable.getName();
	}

	@Override
	public String getDescriptor()
	{
		StringBuilder descriptor = new StringBuilder();

		descriptor.append("(");

		for (Type parameterType: this.getParameterTypes())
		{
			descriptor.append(parameterType.toJVMType());
		}

		descriptor.append(")");

		return descriptor.toString() + this.getReturnType().toJVMType();
	}

	@Override
	public Type getReturnType()
	{
		if (this.executable instanceof Constructor)
		{
			return PrimitiveType.VOID;
		}

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

	@Override
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

	@Override
	public List<MismatchedTypes> checkTypeParameters(ArgumentClasses argumentClasses)
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

	@Override
	public Type getOwner()
	{
		return this.owner;
	}
}
