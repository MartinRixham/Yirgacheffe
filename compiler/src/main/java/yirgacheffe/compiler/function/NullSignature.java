package yirgacheffe.compiler.function;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class NullSignature implements Signature
{
	public boolean isImplementedBy(Signature signature)
	{
		return true;
	}

	public String getDescriptor()
	{
		return null;
	}

	public String getSignature()
	{
		return null;
	}

	public Array<Type> getParameters()
	{
		return new Array<>();
	}

	public String getName()
	{
		return null;
	}

	public Type getReturnType()
	{
		return null;
	}

	public Label getLabel()
	{
		return null;
	}

	public boolean equals(String name, Array<Type> parameters)
	{
		return true;
	}
}
