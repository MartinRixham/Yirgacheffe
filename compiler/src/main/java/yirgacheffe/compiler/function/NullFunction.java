package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class NullFunction implements Function
{
	public boolean isNamed(String name)
	{
		return true;
	}

	public String getName()
	{
		return "";
	}

	public String getDescriptor()
	{
		return null;
	}

	public Type getReturnType()
	{
		return new NullType();
	}

	public Array<Type> getParameterTypes()
	{
		return new Array<>();
	}

	public boolean hasVariableArguments()
	{
		return false;
	}

	public Signature getSignature()
	{
		return new NullSignature();
	}

	public Type getOwner()
	{
		return null;
	}

	public Array<java.lang.reflect.Type> getGenericParameterTypes()
	{
		return new Array<>();
	}

	public boolean isStatic()
	{
		return false;
	}
}
