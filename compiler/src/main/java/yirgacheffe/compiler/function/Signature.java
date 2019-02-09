package yirgacheffe.compiler.function;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class Signature
{
	private Type returnType;

	private String name;

	private Array<Type> parameters;

	private Label label = new Label();

	public Signature(Type returnType, String name, Array<Type> parameters)
	{
		this.returnType = returnType;
		this.name = name;
		this.parameters = parameters;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Signature)
		{
			Signature signature = (Signature) other;

			return this.name.equals(signature.name) &&
				this.parameters.equals(signature.parameters);
		}

		throw new RuntimeException();
	}

	@Override
	public int hashCode()
	{
		return this.name.hashCode() + this.parameters.hashCode();
	}

	public boolean equals(String name, Array<Type> parameters)
	{
		return this.name.equals(name) && this.parameters.equals(parameters);
	}

	public String getDescriptor()
	{
		String[] strings = new String[this.parameters.length()];

		for (int i = 0; i < strings.length; i++)
		{
			strings[i] = this.parameters.get(i).toJVMType();
		}

		return "(" + String.join("", strings) + ")" + this.returnType.toJVMType();
	}

	public String getSignature()
	{
		if (this.returnType.hasParameter())
		{
			return this.reallyGetSignature();
		}

		for (Type type: this.parameters)
		{
			if (type.hasParameter())
			{
				return this.reallyGetSignature();
			}
		}

		return null;
	}

	private String reallyGetSignature()
	{
		String[] strings = new String[this.parameters.length()];

		for (int i = 0; i < strings.length; i++)
		{
			strings[i] = this.parameters.get(i).getSignature();
		}

		String returnType = this.returnType.getSignature();

		return "(" + String.join("", strings) + ")" + returnType;
	}

	public Array<Type> getParameters()
	{
		return this.parameters;
	}

	@Override
	public String toString()
	{
		String[] strings = new String[this.parameters.length()];

		for (int i = 0; i < strings.length; i++)
		{
			strings[i] = this.parameters.get(i).toString();
		}

		return this.name + "(" + String.join(",", strings) + ")";
	}

	public Label getLabel()
	{
		return this.label;
	}
}
