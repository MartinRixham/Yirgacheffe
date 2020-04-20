package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClassInterface implements Interface
{
	private Type type;

	private Class<?> clazz;

	public ClassInterface(Type type, Class<?> clazz)
	{
		this.type = type;
		this.clazz = clazz;
	}

	public Set<Function> getConstructors()
	{
		return this.makeFunctions(this.clazz.getDeclaredConstructors());
	}

	public Set<Function> getPublicConstructors()
	{
		return this.makeFunctions(this.clazz.getConstructors());
	}

	public Set<Function> getMethods()
	{
		return this.makeFunctions(this.clazz.getDeclaredMethods());
	}

	public Set<Function> getPublicMethods()
	{
		return this.makeFunctions(this.clazz.getMethods());
	}

	public Set<Field> getFields()
	{
		return new HashSet<>(Arrays.asList(this.clazz.getDeclaredFields()));
	}

	public boolean hasField(String name)
	{
		try
		{
			this.clazz.getDeclaredField(name);

			return true;
		}
		catch (NoSuchFieldException e)
		{
			return false;
		}
	}

	public Field getField(String name)
	{
		try
		{
			return this.clazz.getDeclaredField(name);
		}
		catch (NoSuchFieldException e)
		{
			return null;
		}
	}

	public Set<Field> getPublicFields()
	{
		return null;
	}

	public Set<java.lang.reflect.Type> getGenericInterfaces()
	{
		return new HashSet<>(Arrays.asList(this.clazz.getGenericInterfaces()));
	}

	public Array<TypeVariable<?>> getTypeParameters()
	{
		return new Array<>(this.clazz.getTypeParameters());
	}

	public boolean isInterface()
	{
		return this.clazz.isInterface();
	}

	public boolean hasMethod(String value)
	{
		try
		{
			this.clazz.getMethod(value);

			return true;
		}
		catch (NoSuchMethodException e)
		{
			return false;
		}
	}

	public boolean doesImplement(Class<?> other)
	{
		return other.isAssignableFrom(this.clazz);
	}

	public boolean isImplementedBy(Class<?> other)
	{
		return this.clazz.isAssignableFrom(other);
	}

	public boolean hasDefaultConstructor()
	{
		try
		{
			this.clazz.getConstructor();

			return true;
		}
		catch (NoSuchMethodException e)
		{
			return false;
		}
	}

	@Override
	public String getSimpleName()
	{
		return this.clazz.getSimpleName();
	}

	private Set<Function> makeFunctions(Executable[] executables)
	{
		Set<Function> functions = new HashSet<>();

		for (Executable executable: executables)
		{
			functions.add(new ClassFunction(this.type, executable));
		}

		return functions;
	}

	public boolean equals(Class<?> other)
	{
		return this.clazz.equals(other);
	}

	@Override
	public boolean equals(Object other)
	{
		return other.equals(this.clazz);
	}

	@Override
	public int hashCode()
	{
		return this.clazz.hashCode();
	}
}
