package yirgacheffe.compiler.implementation;

import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Type;

public class BranchImplementation implements Implementation
{
	public Implementation intersect(Implementation implementation)
	{
		return this;
	}

	public boolean implementsMethod(Function method, Type thisType)
	{
		return false;
	}
}
