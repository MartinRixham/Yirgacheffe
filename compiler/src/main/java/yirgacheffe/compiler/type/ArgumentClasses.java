package yirgacheffe.compiler.type;

import org.antlr.v4.runtime.ParserRuleContext;
import yirgacheffe.compiler.error.Error;

import java.lang.reflect.Executable;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class ArgumentClasses
{
	private Type[] argumentClasses;

	private List<Error> errors;

	public ArgumentClasses(Type[] argumentClasses, List<Error> errors)
	{
		this.argumentClasses = argumentClasses;
		this.errors = errors;
	}

	public void checkTypeParameter(
		Executable executable,
		Type owner,
		ParserRuleContext context)
	{
		if (!(owner instanceof ParameterisedType))
		{
			return;
		}

		ParameterisedType type = (ParameterisedType) owner;
		java.lang.reflect.Type[] parameters = executable.getGenericParameterTypes();

		for (int i = 0; i < parameters.length; i++)
		{
			if (parameters[i] instanceof TypeVariable)
			{
				TypeVariable typeVariable = (TypeVariable) parameters[i];

				boolean hasTypeParameter =
					type.hasTypeParameter(
						typeVariable.getName(),
						this.argumentClasses[i]);

				if (!hasTypeParameter)
				{
					String message =
						"Argument of type " + this.argumentClasses[i] +
						" cannot be assigned to generic parameter of type " +
						type.getTypeParameterName(typeVariable.getName()) + ".";

					this.errors.add(new Error(context, message));
				}
			}
		}
	}

	public int matches(Type[] parameterTypes)
	{
		int exactMatches = 0;

		if (parameterTypes.length != this.argumentClasses.length)
		{
			return -1;
		}

		for (int i = 0; i < parameterTypes.length; i++)
		{
			if (!this.argumentClasses[i].isAssignableTo(parameterTypes[i]))
			{
				return -1;
			}

			if (this.argumentClasses[i].toFullyQualifiedType()
				.equals(parameterTypes[i].toFullyQualifiedType()))
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

		return String.join(",", arguments) + ")";
	}
}
