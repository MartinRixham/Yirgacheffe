package yirgacheffe.compiler.implementation;

import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Type;

public class TotalImplementation implements Implementation
{
	public Implementation intersect(Implementation implementation)
	{
		return implementation;
	}

	public boolean implementsMethod(Function method, Type thisType)
	{
		return true;
	}
}
