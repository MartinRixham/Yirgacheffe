package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

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

	public Array<Type> getParameterTypes()
	{
		return new Array<>();
	}

	public Array<MismatchedTypes> checkTypeParameters(ArgumentClasses argumentClasses)
	{
		return new Array<>();
	}

	public Type getOwner()
	{
		return new NullType();
	}
}
