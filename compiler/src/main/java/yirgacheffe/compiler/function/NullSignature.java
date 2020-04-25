package yirgacheffe.compiler.function;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.type.NullType;
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
		return "()V";
	}

	public String getSignature()
	{
		return "()V";
	}

	public Array<Type> getParameters()
	{
		return new Array<>();
	}

	public String getName()
	{
		return "";
	}

	public Type getReturnType()
	{
		return new NullType();
	}

	public Label getLabel()
	{
		return new Label();
	}
}
