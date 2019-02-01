package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public interface Callable
{
	String getName();

	String getDescriptor();

	Type getReturnType();

	Array<Type> getParameterTypes();

	Array<MismatchedTypes> checkTypeParameters(Arguments arguments);
}
