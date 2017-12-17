package yirgacheffe.compiler.type;

public class Parameters
{
	private Type[] parameterTypes;

	public Parameters(Type[] parameterTypes)
	{
		this.parameterTypes = parameterTypes;
	}

	public String getDescriptor()
	{
		StringBuilder descriptor = new StringBuilder("(");

		for (Type type : this.parameterTypes)
		{
			descriptor.append(type.toJVMType());
		}

		descriptor.append(")");

		return descriptor.toString();
	}
}
