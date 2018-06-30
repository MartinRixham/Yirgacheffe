package yirgacheffe.compiler.type;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class ArgumentClasses
{
	private Type[] argumentClasses;

	public ArgumentClasses(Type[] argumentClasses)
	{
		this.argumentClasses = argumentClasses;
	}

	public List<MismatchedTypes> checkTypeParameters(
		java.lang.reflect.Type[] parameters,
		ParameterisedType owner)
	{
		List<MismatchedTypes> mismatchedParameters = new ArrayList<>();

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

					mismatchedParameters.add(mismatchedTypes);
				}
			}
		}

		return mismatchedParameters;
	}

	public int matches(List<Type> parameterTypes)
	{
		int exactMatches = 0;

		if (parameterTypes.size() != this.argumentClasses.length)
		{
			return -1;
		}

		for (int i = 0; i < parameterTypes.size(); i++)
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
		List<String> arguments = new ArrayList<>();

		for (Type argumentClass : this.argumentClasses)
		{
			arguments.add(argumentClass.toString());
		}

		return "(" + String.join(",", arguments) + ")";
	}
}
