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
			Class<?>[] parameterTypes = executable.getParameterTypes();

			if (argumentClasses.matches(parameterTypes))
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
