package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.VariableType;
import yirgacheffe.lang.Array;

import java.lang.reflect.TypeVariable;

public class TypeParameters
{
	private Array<TypeVariable<?>> parameters;

	private ReferenceType owner;

	public TypeParameters(Array<TypeVariable<?>> parameters, ReferenceType owner)
	{
		this.parameters = parameters;
		this.owner = owner;
	}

	public Type getType()
	{
		if (this.parameters.length() == 0)
		{
			return this.owner;
		}
		else
		{
			Array<Type> parameterTypes = this.getTypeParameters(this.parameters);

			return new ParameterisedType(this.owner, parameterTypes);
		}
	}

	private Array<Type> getTypeParameters(Array<TypeVariable<?>> parameters)
	{
		Array<Type> parameterTypes = new Array<>();

		for (TypeVariable<?> parameter: parameters)
		{
			parameterTypes.push(
				new VariableType(parameter.getName()));
		}

		return parameterTypes;
	}
}
