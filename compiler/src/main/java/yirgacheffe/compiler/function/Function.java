package yirgacheffe.compiler.function;

import yirgacheffe.compiler.type.Arguments;
import yirgacheffe.compiler.type.ArrayType;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.ParameterisedType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class Function implements Callable
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

		return this.getType(returned);
	}

	private Type getType(java.lang.reflect.Type type)
	{
		if (type instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) type;
			ReferenceType primaryType =
					new ReferenceType((Class) parameterizedType.getRawType());

			Array<Type> typeParameters = new Array<>();

			for (java.lang.reflect.Type typeArgument:
				parameterizedType.getActualTypeArguments())
			{
				typeParameters.push(this.getType(typeArgument));
			}

			return new ParameterisedType(primaryType, typeParameters);
		}
		if (type instanceof Class)
		{
			return this.getType((Class) type);
		}
		else
		{
			ParameterisedType parameterisedOwner = (ParameterisedType) this.owner;

			Type returnType =
				parameterisedOwner.getTypeParameterClass(type.getTypeName());

			return new GenericType(returnType);
		}
	}

	public Array<Type> getParameterTypes()
	{
		Class<?>[] classes = this.executable.getParameterTypes();
		Array<Type> types = new Array<>();

		for (Class<?> clazz: classes)
		{
			types.push(this.getType(clazz));
		}

		return types;
	}

	private Type getType(Class<?> clazz)
	{
		if (clazz.isArray())
		{
			return new ArrayType(clazz.getName());
		}
		else if (clazz.isPrimitive())
		{
			return PrimitiveType.valueOf(clazz.getName().toUpperCase());
		}
		else
		{
			return new ReferenceType(clazz);
		}
	}

	public Array<MismatchedTypes> checkTypeParameters(Arguments arguments)
	{
		if (this.owner instanceof ParameterisedType)
		{
			ParameterisedType type = (ParameterisedType) this.owner;
			Array<java.lang.reflect.Type> parameters =
				new Array<>(this.executable.getGenericParameterTypes());

			return arguments.checkTypeParameters(parameters, type);
		}
		else
		{
			return new Array<>();
		}
	}

	@Override
	public String toString()
	{
		String function = this.getReturnType() + " " + this.getName();

		Array<String> parameters = new Array<>();

		for (java.lang.reflect.Type parameter:
			this.executable.getGenericParameterTypes())
		{
			parameters.push(this.getType(parameter).toString());
		}

		return function + "(" + parameters.join(",") + ")";
	}

	public boolean hasSignature(Signature other)
	{
		Array<Type> parameters = new Array<>();

		for (java.lang.reflect.Type parameter:
			this.executable.getGenericParameterTypes())
		{
			parameters.push(this.getType(parameter));
		}

		Signature signature =
			new Signature(
				this.getReturnType(),
				this.getName(),
				parameters);

		return signature.equalWithReturnType(other);
	}
}
