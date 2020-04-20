package yirgacheffe.compiler.implementation;

import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class InterfaceImplementation implements Implementation
{
	private Array<Type> interfaces;

	public InterfaceImplementation(Array<Type> interfaces)
	{
		this.interfaces = interfaces;
	}

	public Implementation intersect(Implementation implementation)
	{
		if (implementation instanceof InterfaceImplementation)
		{
			InterfaceImplementation other = (InterfaceImplementation) implementation;
			Array<Type> intersection = new Array<>();

			for (Type interfaceType : this.interfaces)
			{
				if (other.interfaces.contains(interfaceType))
				{
					intersection.push(interfaceType);
				}
			}

			return new InterfaceImplementation(intersection);
		}
		else
		{
			return this;
		}
	}

	public boolean implementsMethod(Function method, Type thisType)
	{
		for (Type interfaceType : this.interfaces)
		{
			for (Function interfaceMethod : interfaceType.reflect().getPublicMethods())
			{
				if (interfaceMethod.equals(method))
				{
					return true;
				}
			}
		}

		return false;
	}
}
