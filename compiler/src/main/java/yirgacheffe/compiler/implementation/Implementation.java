package yirgacheffe.compiler.implementation;

import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Type;

public interface Implementation
{
	Implementation intersect(Implementation implementation);

	boolean implementsMethod(Function method, Type thisType);
}
