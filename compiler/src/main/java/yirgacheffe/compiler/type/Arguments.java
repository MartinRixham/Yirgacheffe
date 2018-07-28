package yirgacheffe.compiler.type;

import yirgacheffe.lang.Array;

import java.lang.reflect.TypeVariable;

public class Arguments
{
	private Array<Type> arguments;

	public Arguments(Array<Type> arguments)
	{
		this.arguments = arguments;
	}

	public Array<MismatchedTypes> checkTypeParameters(
		Array<java.lang.reflect.Type> parameters,
		ParameterisedType owner)
	{
		Array<MismatchedTypes> mismatchedParameters = new Array<>();

		for (int i = 0; i < parameters.length(); i++)
		{
			if (parameters.get(i) instanceof TypeVariable)
			{
				TypeVariable typeVariable = (TypeVariable) parameters.get(i);
				Type argumentType = this.arguments.get(i);

				boolean hasTypeParameter =
					owner.hasTypeParameter(
						typeVariable.getName(),
						argumentType);

				if (!hasTypeParameter)
				{
					MismatchedTypes mismatchedTypes =
						new MismatchedTypes(
							argumentType.toString(),
							owner.getTypeParameterName(typeVariable.getName()));

					mismatchedParameters.push(mismatchedTypes);
				}
			}
		}

		return mismatchedParameters;
	}

	public int matches(Array<Type> parameterTypes)
	{
		int exactMatches = 0;

		if (parameterTypes.length() != this.arguments.length())
		{
			return -1;
		}

		for (int i = 0; i < parameterTypes.length(); i++)
		{
			Type argumentType = this.arguments.get(i);

			if (!argumentType.isAssignableTo(parameterTypes.get(i)))
			{
				return -1;
			}

			if (argumentType.toJVMType().equals(parameterTypes.get(i).toJVMType()))
			{
				exactMatches++;
			}
		}

		return exactMatches;
	}

	@Override
	public String toString()
	{
		Array<String> arguments = new Array<>();

		for (Type argument : this.arguments)
		{
			arguments.push(argument.toString());
		}

		return "(" + String.join(",", arguments) + ")";
	}
}
