package yirgacheffe.compiler.implementation;

import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Type;

public class TotalImplementation implements Implementation
{
	public Implementation intersect(Implementation implementation)
	{
		if (implementation instanceof BranchImplementation)
		{
			return this;
		}
		else
		{
			return implementation;
		}
	}

	public boolean implementsMethod(Function method, Type thisType)
	{
		return true;
	}
}
