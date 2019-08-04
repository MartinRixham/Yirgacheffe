package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

import java.lang.reflect.ParameterizedType;

public interface Type
{
	Class<?> reflectionClass();

	String toJVMType();

	String toFullyQualifiedType();

	int width();

	int getReturnInstruction();

	int getStoreInstruction();

	int getArrayStoreInstruction();

	int getLoadInstruction();

	int getZero();

	boolean isAssignableTo(Type other);

	boolean hasParameter();

	String getSignature();

	boolean isPrimitive();

	Result newArray();

	Result convertTo(Type type);

	Result swapWith(Type type);

	Type intersect(Type type);

	Result compare(BooleanOperator operator, Label label);

	static Type getType(java.lang.reflect.Type type, Type owner)
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
				typeParameters.push(Type.getType(typeArgument, owner));
			}

			return new ParameterisedType(primaryType, typeParameters);
		}
		if (type instanceof Class)
		{
			return Type.getType((Class) type);
		}
		else
		{
			ParameterisedType parameterisedOwner = (ParameterisedType) owner;

			Type returnType =
				parameterisedOwner.getTypeParameterClass(type.getTypeName());

			return new GenericType(returnType);
		}
	}

	static Type getType(Class<?> clazz)
	{
		if (clazz.isArray())
		{
			Type arrayType = Type.getType(clazz.getComponentType());

			return new ArrayType(clazz.getName(), arrayType);
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
}
