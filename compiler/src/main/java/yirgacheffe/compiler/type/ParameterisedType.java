package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ParameterisedType implements Type
{
	private ReferenceType primaryType;

	private Map<String, Type> typeParameters;

	public ParameterisedType(ReferenceType primaryType, List<Type> typeParameters)
	{
		this.primaryType = primaryType;

		TypeVariable[] genericTypes = primaryType.reflectionClass().getTypeParameters();
		Map<String, Type> types = new HashMap<>();

		for (int i = 0; i < typeParameters.size(); i++)
		{
			types.put(genericTypes[i].getName(), typeParameters.get(i));
		}

		this.typeParameters = types;
	}

	@Override
	public Class<?> reflectionClass()
	{
		return this.primaryType.reflectionClass();
	}

	@Override
	public String toJVMType()
	{
		return "L" + this.toFullyQualifiedType().replace('.', '/')  + ";";
	}

	@Override
	public String toFullyQualifiedType()
	{
		return this.primaryType.toFullyQualifiedType();
	}

	@Override
	public int width()
	{
		return 1;
	}

	@Override
	public int getReturnInstruction()
	{
		return Opcodes.ARETURN;
	}

	@Override
	public int getStoreInstruction()
	{
		return Opcodes.ASTORE;
	}

	@Override
	public int getLoadInstruction()
	{
		return Opcodes.ALOAD;
	}

	@Override
	public String toString()
	{
		List<String> typeNames = new ArrayList<>();

		for (String key: this.typeParameters.keySet())
		{
			typeNames.add(this.typeParameters.get(key).toFullyQualifiedType());
		}

		return this.toFullyQualifiedType() +
			"<" + String.join(",", typeNames) + ">";
	}

	@Override
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
			.reflectionClass()
			.getName();
	}
}