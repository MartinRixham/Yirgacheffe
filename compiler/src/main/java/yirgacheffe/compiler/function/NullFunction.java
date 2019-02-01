package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class NullFunction implements Callable
{
	private String name;

	public NullFunction(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
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

	public Array<MismatchedTypes> checkTypeParameters(Arguments arguments)
	{
		return new Array<>();
	}
}
