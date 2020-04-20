package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.function.Interface;
import yirgacheffe.compiler.operator.BooleanOperator;

public class IntersectionType implements Type
{
	private Type firstType;

	private Type secondType;

	public IntersectionType(Type firstType, Type secondType)
	{
		this.firstType = firstType;
		this.secondType = secondType;
	}

	public Interface reflect()
	{
		return this.firstType.reflect(this);
	}

	public Interface reflect(Type type)
	{
		return this.firstType.reflect(type);
	}

	public String toJVMType()
	{
		return this.firstType.toJVMType();
	}

	public String toFullyQualifiedType()
	{
		return this.firstType.toFullyQualifiedType();
	}

	public int width()
	{
		return 1;
	}

	public int getReturnInstruction()
	{
		return this.firstType.getReturnInstruction();
	}

	public int getStoreInstruction()
	{
		return this.firstType.getStoreInstruction();
	}

	public int getArrayStoreInstruction()
	{
		return this.firstType.getArrayStoreInstruction();
	}

	public int getLoadInstruction()
	{
		return this.firstType.getLoadInstruction();
	}

	public int getZero()
	{
		return this.firstType.getZero();
	}

	public String toString()
	{
		return this.firstType.toString();
	}

	public boolean isAssignableTo(Type other)
	{
		return this.firstType.isAssignableTo(other) &&
			this.secondType.isAssignableTo(other);
	}

	public boolean hasParameter()
	{
		return this.firstType.hasParameter();
	}

	public String getSignature()
	{
		return this.firstType.getSignature();
	}

	public boolean isPrimitive()
	{
		return false;
	}

	public Result newArray()
	{
		return this.firstType.newArray();
	}

	public Result convertTo(Type type)
	{
		return this.firstType.convertTo(type);
	}

	public Result swapWith(Type type)
	{
		return new Result();
	}

	public Type intersect(Type type)
	{
		return new IntersectionType(this, type);
	}

	public Result compare(BooleanOperator operator, Label label)
	{
		return this.firstType.compare(operator, label);
	}

	public Type getTypeParameter(String typeName)
	{
		return new NullType();
	}

	@Override
	public boolean equals(Object other)
	{
		return this.firstType.equals(other) && this.secondType.equals(other);
	}

	@Override
	public int hashCode()
	{
		return this.secondType.hashCode();
	}
}
