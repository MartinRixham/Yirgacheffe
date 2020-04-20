package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.BoundedType;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.VariableType;
import yirgacheffe.lang.Array;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;

public class Parameters
{
	private Array<TypeVariable<?>> parameters;

	private ReferenceType owner;

	public Parameters(Array<TypeVariable<?>> parameters, ReferenceType owner)
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
			java.lang.reflect.Type[] typeBounds = parameter.getBounds();
			Type typeBound;

			if (typeBounds.length == 0)
			{
				typeBound = new ReferenceType(Object.class);
			}
			else
			{
				typeBound = getType(typeBounds[0]);
			}

			parameterTypes.push(
				new BoundedType(parameter.getName(), typeBound));
		}

		return parameterTypes;
	}

	private Type getType(java.lang.reflect.Type type)
	{
		if (type instanceof Class)
		{
			return new ReferenceType((Class<?>) type);
		}
		else if (type instanceof ParameterizedType)
		{
			ParameterizedType parameterisedType = (ParameterizedType) type;

			ReferenceType primaryType =
				new ReferenceType((Class<?>) parameterisedType.getRawType());

			java.lang.reflect.Type[] typeArguments =
				parameterisedType.getActualTypeArguments();

			Array<Type> arguments = new Array<>();

			for (java.lang.reflect.Type argument: typeArguments)
			{
				arguments.push(this.getType(argument));
			}

			return new ParameterisedType(primaryType, arguments);
		}
		else
		{
			return new VariableType(type.getTypeName());
		}
	}
}
