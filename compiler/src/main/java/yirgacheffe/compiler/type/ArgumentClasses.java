package yirgacheffe.compiler.type;

import org.antlr.v4.runtime.ParserRuleContext;
import yirgacheffe.compiler.error.Error;

import java.lang.reflect.Executable;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class ArgumentClasses
{
	private Class<?>[] argumentClasses;

	private List<Error> errors;

	public ArgumentClasses(Class<?>[] argumentClasses, List<Error> errors)
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
			if (parameters[i] instanceof TypeVariable &&
				!type.hasTypeParameter(this.argumentClasses[i]))
			{
				String message =
					"Argument of type " + this.argumentClasses[i].getName() +
						" cannot be assigned to generic parameter of type " +
						type.getTypeParameterName() + ".";

				this.errors.add(new Error(context, message));
			}
		}
	}

	public boolean matches(Class<?>[] parameterTypes)
	{
		if (parameterTypes.length != this.argumentClasses.length)
		{
			return false;
		}

		for (int i = 0; i < parameterTypes.length; i++)
		{
			if (!parameterTypes[i].isAssignableFrom(this.argumentClasses[i]) &&
				!parameterTypes[i].isPrimitive())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString()
	{
		List<String> arguments = new ArrayList<>();

		for (Class<?> argumentClass : this.argumentClasses)
		{
			arguments.add(argumentClass.getName());
		}

		return String.join(",", arguments) + ")";
	}
}
