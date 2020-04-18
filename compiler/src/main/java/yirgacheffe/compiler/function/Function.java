package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public interface Function
{
	boolean isNamed(String name);

	String getName();

	String getDescriptor();

	Type getReturnType();

	Array<Type> getParameterTypes();

	boolean hasVariableArguments();

	Signature getSignature();

	Type getOwner();

	Array<java.lang.reflect.Type> getGenericParameterTypes();
}
