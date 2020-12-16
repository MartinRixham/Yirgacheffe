package yirgacheffe.compiler.type;

import yirgacheffe.lang.Array;

public class ClassSignature
{
	private static final String OBJECT = "Ljava/lang/Object;";

	private Array<Type> interfaces;

	private Array<BoundedType> parameters;

	public ClassSignature(Array<Type> interfaces, Array<BoundedType> parameters)
	{
		this.interfaces = interfaces;
		this.parameters = parameters;
	}

	@Override
	public String toString()
	{
		if (this.interfaces.length() == 0 && this.parameters.length() == 0)
		{
			return null;
		}

		StringBuilder signature = new StringBuilder();

		if (this.parameters.length() > 0)
		{
			signature.append("<");
		}

		for (BoundedType parameter: this.parameters)
		{
			signature.append(parameter.getSignature());
		}

		if (this.parameters.length() > 0)
		{
			signature.append(">");
		}

		signature.append(OBJECT);

		for (Type type: this.interfaces)
		{
			signature.append(type.getSignature());
		}

		return signature.toString();
	}
}
