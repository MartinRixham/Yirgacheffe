package yirgacheffe.compiler.type;

import yirgacheffe.lang.Array;

import java.util.Set;

public class ConstantType
{
	private Type constantType = new NullType();

	public ConstantType(Type type)
	{
		Set<java.lang.reflect.Type> interfaces =
			type.reflect().getGenericInterfaces();

		for (java.lang.reflect.Type interfaceType: interfaces)
		{
			if (interfaceType.getTypeName().startsWith("yirgacheffe.lang.Enumeration"))
			{
				this.constantType =
					Type.getType(interfaceType, type).getTypeParameter("T");
			}
		}
	}

	public boolean matches(Type type)
	{
		if (type.isAssignableTo(PrimitiveType.DOUBLE))
		{
			return new Array<>(
				new ReferenceType(Integer.class),
				new ReferenceType(Long.class),
				new ReferenceType(Double.class))
				.contains(this.constantType);
		}
		else
		{
			return type.isAssignableTo(this.constantType);
		}
	}

	@Override
	public String toString()
	{
		return this.constantType.toString();
	}
}
