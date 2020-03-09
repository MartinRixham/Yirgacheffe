package yirgacheffe.compiler.implementation;

import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Type;

public class BranchImplementation implements Implementation
{
	private Implementation implementation;

	public BranchImplementation(Implementation implementation)
	{
		this.implementation = implementation;
	}

	public Implementation intersect(Implementation other)
	{
		if (other instanceof BranchImplementation)
		{
			BranchImplementation branchImplementation = (BranchImplementation) other;

			return new BranchImplementation(
				this.implementation.intersect(branchImplementation.implementation));
		}
		else
		{
			return this;
		}
	}

	public boolean implementsMethod(Function method, Type thisType)
	{
		return implementation.implementsMethod(method, thisType);
	}
}
