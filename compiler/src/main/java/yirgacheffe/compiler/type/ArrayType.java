package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;

public class ArrayType implements Type
{
	private String jvmType;

	private String fullyQualifiedType;

	private Class<?> reflectionClass;

	private Type type;

	public ArrayType(String name, Type type)
	{
		this.jvmType = name.replace(".", "/");
		this.fullyQualifiedType = name.substring(2).replace(";", "[]");
		this.type = type;

		try
		{
			this.reflectionClass = Class.forName(name);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Class<?> reflectionClass()
	{
		return this.reflectionClass;
	}

	public String toJVMType()
	{
		return this.jvmType;
	}

	public String toFullyQualifiedType()
	{
		return this.fullyQualifiedType;
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
		return this.fullyQualifiedType;
	}

	public boolean isAssignableTo(Type other)
	{
		if (other instanceof ArrayType)
		{
			ArrayType otherType = (ArrayType) other;

			return this.type.isAssignableTo(otherType.type);
		}
		else
		{
			return false;
		}
	}

	public boolean hasParameter()
	{
		return false;
	}

	public String getSignature()
	{
		return this.jvmType;
	}

	public boolean isPrimitive()
	{
		return false;
	}

	public Result newArray()
	{
		return new Result();
	}

	public Result convertTo(Type type)
	{
		return new Result();
	}

	public Result swapWith(Type type)
	{
		return new Result();
	}

	public Type intersect(Type type)
	{
		return new ReferenceType(Object.class);
	}

	public Result compare(BooleanOperator operator, Label label)
	{
		return new Result();
	}

	public Type getElementType()
	{
		return this.type;
	}

	@Override
	public boolean equals(Object other)
	{
		Type type = (Type) other;

		return this.reflectionClass.equals(type.reflectionClass());
	}

	@Override
	public int hashCode()
	{
		return this.reflectionClass.hashCode();
	}
}
