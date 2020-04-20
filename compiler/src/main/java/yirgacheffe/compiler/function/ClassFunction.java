package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassFunction implements Function
{
	private Type owner;

	private Executable executable;

	public ClassFunction(Type owner, Executable executable)
	{
		this.owner = owner;
		this.executable = executable;
	}

	public boolean isNamed(String name)
	{
		return this.getName().equals(name);
	}

	public String getName()
	{
		return this.executable.getName();
	}

	public String getDescriptor()
	{
		StringBuilder descriptor = new StringBuilder();

		descriptor.append("(");

		for (Type parameterType: this.getParameterTypes())
		{
			descriptor.append(parameterType.toJVMType());
		}

		descriptor.append(")");

		return descriptor.toString() + this.getReturnType().toJVMType();
	}

	public Type getReturnType()
	{
		if (this.executable instanceof Constructor)
		{
			return PrimitiveType.VOID;
		}

		Method method = (Method) this.executable;
		java.lang.reflect.Type returned = method.getGenericReturnType();

		return Type.getType(returned, this.owner);
	}

	public Array<Type> getParameterTypes()
	{
		java.lang.reflect.Type[] parameters = this.executable.getGenericParameterTypes();
		Array<Type> types = new Array<>();

		for (java.lang.reflect.Type type: parameters)
		{
			types.push(Type.getType(type, this.owner));
		}

		return types;
	}

	public boolean hasVariableArguments()
	{
		return Modifier.isTransient(this.executable.getModifiers());
	}

	public Signature getSignature()
	{
		Array<Type> parameters = new Array<>();

		for (java.lang.reflect.Type parameter:
			this.executable.getGenericParameterTypes())
		{
			parameters.push(Type.getType(parameter, this.owner));
		}

		return new FunctionSignature(
			this.getReturnType(),
			this.getName(),
			parameters);
	}

	public Type getOwner()
	{
		return this.owner;
	}

	public Array<java.lang.reflect.Type> getGenericParameterTypes()
	{
		return new Array<>(this.executable.getGenericParameterTypes());
	}

	public boolean isStatic()
	{
		return Modifier.isStatic(this.executable.getModifiers());
	}

	@Override
	public String toString()
	{
		String function = this.getReturnType() + " " + this.getName();
		Array<String> parameters = new Array<>();

		for (java.lang.reflect.Type parameter:
			this.executable.getGenericParameterTypes())
		{
			parameters.push(Type.getType(parameter, this.owner).toString());
		}

		return function + "(" + parameters.join(",") + ")";
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ClassFunction)
		{
			ClassFunction otherFunction = (ClassFunction) other;

			return this.executable.equals(otherFunction.executable);
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return this.executable.hashCode();
	}
}
