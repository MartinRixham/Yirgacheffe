package yirgacheffe.compiler.type;

import java.lang.reflect.Executable;
import java.util.List;

public class Executables<T extends Executable>
{
	private List<T> executables;

	public Executables(List<T> executables)
	{
		this.executables = executables;
	}

	public T getExecutable(
		ArgumentClasses argumentClasses,
		StringBuilder argumentDescriptor)
	{
		for (T executable: this.executables)
		{
			Type[] parameterTypes = this.getTypes(executable.getParameterTypes());

			if (argumentClasses.matches(parameterTypes))
			{
				argumentDescriptor.append("(");

				for (Type parameterType: parameterTypes)
				{
						argumentDescriptor.append(parameterType.toJVMType());
				}

				argumentDescriptor.append(")");

				return executable;
			}
		}

		return null;
	}

	private Type[] getTypes(Class<?>[] classes)
	{
		Type[] types = new Type[classes.length];

		for (int i = 0; i < classes.length; i++)
		{
			if (classes[i].isArray())
			{
				types[i] = new ArrayType(classes[i].getName());
			}
			else if (classes[i].isPrimitive())
			{
				types[i] = PrimitiveType.valueOf(classes[i].getName().toUpperCase());
			}
			else
			{
				types[i] = new ReferenceType(classes[i]);
			}
		}

		return types;
	}
}
