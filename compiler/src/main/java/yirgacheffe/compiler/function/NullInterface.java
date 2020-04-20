package yirgacheffe.compiler.function;

import yirgacheffe.lang.Array;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NullInterface implements Interface
{
	public Set<Function> getConstructors()
	{
		return new HashSet<>(Collections.singletonList(new NullFunction()));
	}

	public Set<Function> getPublicConstructors()
	{
		return new HashSet<>(Collections.singletonList(new NullFunction()));
	}

	public Set<Function> getMethods()
	{
		return new HashSet<>(Collections.singletonList(new NullFunction()));
	}

	public Set<Function> getPublicMethods()
	{
		return new HashSet<>(Collections.singletonList(new NullFunction()));
	}

	public Set<Field> getFields()
	{
		return new HashSet<>();
	}

	public boolean hasField(String name)
	{
		return true;
	}

	public Field getField(String name)
	{
		return null;
	}

	public Set<Type> getGenericInterfaces()
	{
		return new HashSet<>();
	}

	public Array<TypeVariable<?>> getTypeParameters()
	{
		return new Array<>();
	}

	public boolean isInterface()
	{
		return false;
	}

	public boolean hasMethod(String value)
	{
		return true;
	}

	public boolean doesImplement(Class<?> other)
	{
		return true;
	}

	public boolean isImplementedBy(Class<?> reflectionClass)
	{
		return true;
	}

	public boolean hasDefaultConstructor()
	{
		return true;
	}

	public String getSimpleName()
	{
		return "";
	}
}
