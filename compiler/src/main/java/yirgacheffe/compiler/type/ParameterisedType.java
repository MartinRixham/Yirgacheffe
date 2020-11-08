package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParameterisedType implements Type
{
	private ReferenceType primaryType;

	private Map<String, Type> typeParameters;

	public ParameterisedType(ReferenceType primaryType, Array<Type> typeParameters)
	{
		this.primaryType = primaryType;

		Array<TypeVariable<?>> genericTypes = primaryType.reflect().getTypeParameters();
		Map<String, Type> types = new LinkedHashMap<>();
		int typeCount = Math.min(genericTypes.length(), typeParameters.length());

		for (int i = 0; i < typeCount; i++)
		{
			types.put(genericTypes.get(i).getName(), typeParameters.get(i));
		}

		this.typeParameters = types;
	}

	public Interface reflect()
	{
		return this.primaryType.reflect(this);
	}

	public Interface reflect(Type type)
	{
		return this.primaryType.reflect(type);
	}

	public String toJVMType()
	{
		return "L" + this.toFullyQualifiedType() + ";";
	}

	public String toFullyQualifiedType()
	{
		return this.primaryType.toFullyQualifiedType();
	}

	public Result construct(Coordinate coordinate)
	{
		Result result =  this.primaryType.construct(coordinate);

		Array<TypeVariable<?>> parameters =
			this.primaryType.reflect().getTypeParameters();

		for (TypeVariable<?> parameter: parameters)
		{
			if (this.typeParameters.containsKey(parameter.getName()))
			{
				Type typeParameter = this.typeParameters.get(parameter.getName());

				result = result.concat(typeParameter.construct(coordinate));

				Type bound = Type.getType(parameter.getBounds()[0], this);

				if (!typeParameter.isAssignableTo(bound))
				{
					String message = "Type parameter " + typeParameter +
						" does not satisfy type bound " + bound + ".";

					result = result.add(new Error(coordinate, message));
				}
			}
		}

		return result;
	}

	public int width()
	{
		return 1;
	}

	public int getReturnInstruction()
	{
		return Opcodes.ARETURN;
	}

	public int getStoreInstruction()
	{
		return Opcodes.ASTORE;
	}

	public int getArrayStoreInstruction()
	{
		return Opcodes.AASTORE;
	}

	public int getLoadInstruction()
	{
		return Opcodes.ALOAD;
	}

	public int getZero()
	{
		return Opcodes.ACONST_NULL;
	}

	public String toString()
	{
		Array<String> typeNames = new Array<>();
		Interface members = this.primaryType.reflect();

		for (TypeVariable type: members.getTypeParameters())
		{
			if (this.typeParameters.containsKey(type.getName()))
			{
				typeNames.push(this.typeParameters.get(type.getName()).toString());
			}
		}

		return this.primaryType +
			"<" + String.join(",", typeNames) + ">";
	}

	public boolean isAssignableTo(Type other)
	{
		if (other instanceof ParameterisedType)
		{
			ParameterisedType parameterisedType = (ParameterisedType) other;
			Collection<Type> parameters = this.typeParameters.values();
			Collection<Type> otherParameters = parameterisedType.typeParameters.values();

			if (!this.primaryType.isAssignableTo(parameterisedType.primaryType) ||
				parameters.size() != otherParameters.size())
			{
				return false;
			}

			Iterator<Type> iterator = parameters.iterator();
			Iterator<Type> otherIterator = otherParameters.iterator();

			while (iterator.hasNext())
			{
				if (!iterator.next().isAssignableTo(otherIterator.next()))
				{
					return false;
				}
			}

			return true;
		}
		else
		{
			return this.primaryType.isAssignableTo(other);
		}
	}

	public boolean hasParameter()
	{
		return true;
	}

	public String getSignature()
	{
		Array<String> typeNames = new Array<>();
		Interface members = this.primaryType.reflect();

		for (TypeVariable<?> type: members.getTypeParameters())
		{
			Type parameterType = this.typeParameters.get(type.getName());

			if (parameterType != null)
			{
				if (parameterType.isPrimitive())
				{
					typeNames.push("L" + parameterType.toFullyQualifiedType() + ";");
				}
				else
				{
					typeNames.push(parameterType.getSignature());
				}
			}
		}

		String jvmType = this.toJVMType();

		return jvmType.substring(0, jvmType.length() - 1) +
			"<" + String.join("", typeNames) + ">;";
	}

	public boolean isPrimitive()
	{
		return false;
	}

	public Result newArray()
	{
		return this.primaryType.newArray();
	}

	public Result convertTo(Type type)
	{
		return this.primaryType.convertTo(type);
	}

	public Result swapWith(Type type)
	{
		return this.primaryType.swapWith(type);
	}

	public Type intersect(Type type)
	{
		return new ReferenceType(Object.class);
	}

	public Result compare(BooleanOperator operator, Label label)
	{
		return this.primaryType.compare(operator, label);
	}

	public boolean hasTypeParameter(String typeName, Type genericParameterType)
	{
		Type type = this.typeParameters.get(typeName);

		if (type == null)
		{
			return true;
		}

		return genericParameterType.isAssignableTo(type);
	}

	public String getTypeParameterName(String typeName)
	{
		return this.typeParameters
			.get(typeName)
			.toString();
	}

	public Type getTypeParameter(String typeName)
	{
		if (this.typeParameters.containsKey(typeName))
		{
			return this.typeParameters.get(typeName);
		}
		else
		{
			return new NullType();
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Type)
		{
			Type otherType = (Type) other;

			return this.isAssignableTo(otherType) && otherType.isAssignableTo(this);
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return this.primaryType.hashCode();
	}
}
