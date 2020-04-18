package yirgacheffe.compiler.function;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class FunctionSignature implements Signature
{
	private Type returnType;

	private String name;

	private Array<Type> parameters;

	private Label label = new Label();

	public FunctionSignature(Type returnType, String name, Array<Type> parameters)
	{
		this.returnType = returnType;
		this.name = name;
		this.parameters = parameters;
	}

	public boolean isImplementedBy(Signature signature)
	{
		if (this.name.equals(signature.getName()) &&
			signature.getReturnType().isAssignableTo(this.returnType) &&
			this.parameters.length() == signature.getParameters().length())
		{
			for (int i = 0; i < this.parameters.length(); i++)
			{
				if (!this.parameters.get(i).isAssignableTo(
					signature.getParameters().get(i)))
				{
					return false;
				}
			}

			return true;
		}

		return false;
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

	public String getName()
	{
		return this.name;
	}

	public Type getReturnType()
	{
		return this.returnType;
	}

	public Label getLabel()
	{
		return this.label;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Signature)
		{
			Signature signature = (Signature) other;

			if (!this.name.equals(signature.getName()) ||
				this.parameters.length() != signature.getParameters().length())
			{
				return false;
			}

			for (int i = 0; i < this.parameters.length(); i++)
			{
				if (!this.parameters.get(i).toJVMType().equals(
					signature.getParameters().get(i).toJVMType()))
				{
					return false;
				}
			}

			return true;
		}

		throw new RuntimeException();
	}

	public boolean equals(String name, Array<Type> parameters)
	{
		return this.name.equals(name) && this.parameters.equals(parameters);
	}

	@Override
	public int hashCode()
	{
		return this.name.hashCode() + this.parameters.hashCode();
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
}
