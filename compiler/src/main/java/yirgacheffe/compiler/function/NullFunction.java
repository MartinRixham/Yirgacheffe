package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;

import java.util.ArrayList;
import java.util.List;

public class NullFunction implements Callable
{
	public String getName()
	{
		return "";
	}

	public String getDescriptor()
	{
		return "()V";
	}

	public Type getReturnType()
	{
		return new NullType();
	}

	public List<Type> getParameterTypes()
	{
		return new ArrayList<>();
	}

	public List<MismatchedTypes> checkTypeParameters(ArgumentClasses argumentClasses)
	{
		return new ArrayList<>();
	}

	public Type getOwner()
	{
		return new NullType();
	}
}
