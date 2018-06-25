package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.ArgumentClasses;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.Type;

import java.util.List;

public interface Callable
{
	String getName();

	String getDescriptor();

	Type getReturnType();

	List<Type> getParameterTypes();

	List<MismatchedTypes> checkTypeParameters(ArgumentClasses argumentClasses);

	Type getOwner();
}
