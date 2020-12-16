package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.BoundedType;
import yirgacheffe.compiler.type.Type;

import java.util.Map;

public class Caller
{
	private String name;

	private Map<String, BoundedType> typeParameters;

	public Caller(String name, Map<String, BoundedType> typeParameters)
	{
		this.name = name;
		this.typeParameters = typeParameters;
	}

	public boolean equals(Type type)
	{
		return type.toFullyQualifiedType().equals(this.name);
	}

	public Type lookup(Type type)
	{
		if (this.typeParameters.containsKey(type.toString()))
		{
			return this.typeParameters.get(type.toString());
		}
		else
		{
			return type;
		}
	}
}
