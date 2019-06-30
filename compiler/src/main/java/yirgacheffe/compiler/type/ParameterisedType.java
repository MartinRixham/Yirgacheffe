package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.lang.Array;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class ParameterisedType implements Type
{
	private ReferenceType primaryType;

	private Map<String, Type> typeParameters;

	public ParameterisedType(ReferenceType primaryType, Array<Type> typeParameters)
	{
		this.primaryType = primaryType;

		TypeVariable[] genericTypes = primaryType.reflectionClass().getTypeParameters();
		Map<String, Type> types = new HashMap<>();
		int typeCount = Math.min(genericTypes.length, typeParameters.length());

		for (int i = 0; i < typeCount; i++)
		{
			types.put(genericTypes[i].getName(), typeParameters.get(i));
		}

		this.typeParameters = types;
	}

	public Class<?> reflectionClass()
	{
		return this.primaryType.reflectionClass();
	}

	public String toJVMType()
	{
		return "L" + this.toFullyQualifiedType() + ";";
	}

	public String toFullyQualifiedType()
	{
		return this.primaryType.toFullyQualifiedType();
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
		Class<?> primaryClass = this.primaryType.reflectionClass();

		for (TypeVariable type: primaryClass.getTypeParameters())
		{
			typeNames.push(this.typeParameters.get(type.getName()).toString());
		}

		return this.primaryType +
			"<" + String.join(",", typeNames) + ">";
	}

	public boolean isAssignableTo(Type other)
	{
		if (other instanceof ParameterisedType)
		{
			ParameterisedType parameterisedType = (ParameterisedType) other;

			for (String key: this.typeParameters.keySet())
			{
				if (!this.typeParameters.get(key)
					.isAssignableTo(parameterisedType.typeParameters.get(key)))
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
		Class<?> primaryClass = this.primaryType.reflectionClass();

		for (TypeVariable type: primaryClass.getTypeParameters())
		{
			typeNames.push(this.typeParameters.get(type.getName()).getSignature());
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

	public Type getTypeParameterClass(String typeName)
	{
		return this.typeParameters.get(typeName);
	}
}
