package yirgacheffe.compiler.type;

import yirgacheffe.lang.Array;

import java.lang.reflect.TypeVariable;

public class ArgumentClasses
{
	private Type[] argumentClasses;

	public ArgumentClasses(Type[] argumentClasses)
	{
		this.argumentClasses = argumentClasses;
	}

	public Array<MismatchedTypes> checkTypeParameters(
		java.lang.reflect.Type[] parameters,
		ParameterisedType owner)
	{
		Array<MismatchedTypes> mismatchedParameters = new Array<>();

		for (int i = 0; i < parameters.length; i++)
		{
			if (parameters[i] instanceof TypeVariable)
			{
				TypeVariable typeVariable = (TypeVariable) parameters[i];

				boolean hasTypeParameter =
					owner.hasTypeParameter(
						typeVariable.getName(),
						this.argumentClasses[i]);

				if (!hasTypeParameter)
				{
					MismatchedTypes mismatchedTypes =
						new MismatchedTypes(
							this.argumentClasses[i].toString(),
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

		if (parameterTypes.length() != this.argumentClasses.length)
		{
			return -1;
		}

		for (int i = 0; i < parameterTypes.length(); i++)
		{
			if (!this.argumentClasses[i].isAssignableTo(parameterTypes.get(i)))
			{
				return -1;
			}

			if (this.argumentClasses[i].toJVMType()
				.equals(parameterTypes.get(i).toJVMType()))
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

		for (Type argumentClass : this.argumentClasses)
		{
			arguments.push(argumentClass.toString());
		}

		return "(" + String.join(",", arguments) + ")";
	}
}
