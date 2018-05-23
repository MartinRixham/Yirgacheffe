package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;

public class ParameterisedType implements Type
{
	private ReferenceType primaryType;

	private Type typeParameter;

	public ParameterisedType(ReferenceType primaryType, Type typeParameter)
	{
		this.primaryType = primaryType;
		this.typeParameter = typeParameter;
	}

	@Override
	public Class<?> reflectionClass()
	{
		return this.primaryType.reflectionClass();
	}

	public String toJVMType()
	{
		return "L" + this.toFullyQualifiedType().replace('.', '/')  + ";";
	}

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
		return this.toFullyQualifiedType() +
			"<" + this.typeParameter.toFullyQualifiedType() + ">";
	}

	@Override
	public boolean isAssignableTo(Type other)
	{
		if (other instanceof ParameterisedType)
		{
			ParameterisedType parameterisedType = (ParameterisedType) other;

			return this.primaryType.isAssignableTo(parameterisedType.primaryType) &&
				this.typeParameter.isAssignableTo(parameterisedType.typeParameter);
		}
		else
		{
			return false;
		}
	}

	public boolean hasTypeParameter(Class<?> genericParameterType)
	{
		return this.typeParameter
			.reflectionClass()
			.isAssignableFrom(genericParameterType);
	}

	public String getTypeParameterName()
	{
		return this.typeParameter
			.reflectionClass()
			.getName();
	}
}
