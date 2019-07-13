package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;

public class GenericType implements Type
{
	private Type type;

	public GenericType(Type type)
	{
		this.type = type;
	}

	public Class<?> reflectionClass()
	{
		return this.type.reflectionClass();
	}

	public String toJVMType()
	{
		return "Ljava/lang/Object;";
	}

	public String toFullyQualifiedType()
	{
		return this.type.toFullyQualifiedType();
	}

	public int width()
	{
		return this.type.width();
	}

	public int getReturnInstruction()
	{
		return this.type.getReturnInstruction();
	}

	public int getStoreInstruction()
	{
		return this.type.getStoreInstruction();
	}

	public int getArrayStoreInstruction()
	{
		return this.type.getArrayStoreInstruction();
	}

	public int getLoadInstruction()
	{
		return this.type.getLoadInstruction();
	}

	public int getZero()
	{
		return this.type.getZero();
	}

	public String toString()
	{
		return this.type.toString();
	}

	public boolean isAssignableTo(Type other)
	{
		return this.type.isAssignableTo(other);
	}

	public boolean hasParameter()
	{
		return this.type.hasParameter();
	}

	public String getSignature()
	{
		return this.type.getSignature();
	}

	public boolean isPrimitive()
	{
		return this.type.isPrimitive();
	}

	public Result newArray()
	{
		return this.type.newArray();
	}

	public Result convertTo(Type type)
	{
		return this.type.convertTo(type);
	}

	public Result swapWith(Type type)
	{
		return this.type.swapWith(type);
	}

	public Type intersect(Type type)
	{
		return new ReferenceType(Object.class);
	}

	public Result compare(BooleanOperator operator, Label label)
	{
		return this.type.compare(operator, label);
	}

	public Type unwrap()
	{
		return this.type;
	}

	@Override
	public boolean equals(Object other)
	{
		return this.type.equals(other);
	}

	@Override
	public int hashCode()
	{
		return this.type.hashCode();
	}
}
