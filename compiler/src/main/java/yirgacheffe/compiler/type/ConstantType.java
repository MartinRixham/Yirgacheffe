package yirgacheffe.compiler.type;

import yirgacheffe.lang.Array;

public class ConstantType
{
	private Type constantType = new NullType();

	public ConstantType(Type type)
	{
		java.lang.reflect.Type[] interfaces =
			type.reflectionClass().getGenericInterfaces();

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
			return type.isAssignableTo(this.constantType) ||
				type.reflectionClass().isAssignableFrom(
					this.constantType.reflectionClass());
		}
	}

	@Override
	public String toString()
	{
		return this.constantType.toString();
	}
}
