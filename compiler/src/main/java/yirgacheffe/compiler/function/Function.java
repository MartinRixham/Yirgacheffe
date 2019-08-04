package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Function
{
	private Type owner;

	private Executable executable;

	public Function(Type owner, Executable executable)
	{
		this.owner = owner;
		this.executable = executable;
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
		Class<?>[] classes = this.executable.getParameterTypes();
		Array<Type> types = new Array<>();

		for (Class<?> clazz: classes)
		{
			types.push(Type.getType(clazz));
		}

		return types;
	}

	public boolean hasVariableArguments()
	{
		return Modifier.isTransient(this.executable.getModifiers());
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

	public Signature getSignature()
	{
		Array<Type> parameters = new Array<>();

		for (java.lang.reflect.Type parameter:
			this.executable.getGenericParameterTypes())
		{
			parameters.push(Type.getType(parameter, this.owner));
		}

		return new Signature(
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
}
