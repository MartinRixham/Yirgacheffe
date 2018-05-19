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

	public T getExecutable(Class<?>[] argumentClasses, StringBuilder argumentDescriptor)
	{
		for (T executable: this.executables)
		{
			Class<?>[] parameterTypes = executable.getParameterTypes();
			boolean matched = true;

			if (parameterTypes.length != argumentClasses.length)
			{
				continue;
			}

			for (int i = 0; i < parameterTypes.length; i++)
			{
				if (!parameterTypes[i].isAssignableFrom(argumentClasses[i]) &&
					!parameterTypes[i].getSimpleName().equals(
						argumentClasses[i].getSimpleName().toLowerCase()))
				{
					matched = false;
					break;
				}
			}

			if (matched)
			{
				argumentDescriptor.append("(");

				for (Class<?> parameterType: parameterTypes)
				{
					if (parameterType.isPrimitive())
					{
						argumentDescriptor.append(
							PrimitiveType.valueOf(parameterType.getName().toUpperCase())
								.toJVMType());
					}
					else if (parameterType.isArray())
					{
						argumentDescriptor.append(
							new ArrayType(parameterType.getName()).toJVMType());
					}
					else
					{
						argumentDescriptor.append(
							new ReferenceType(parameterType).toJVMType());
					}
				}

				argumentDescriptor.append(")");

				return executable;
			}
		}

		return null;
	}
}
