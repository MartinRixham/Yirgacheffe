package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.operator.BooleanOperator;

public class BoundedType implements Type
{
	private String name;

	private Type typeBound;

	public BoundedType(String name, Type typeBound)
	{
		this.name = name;
		this.typeBound = typeBound;
	}

	public Interface reflect()
	{
		return this.typeBound.reflect(this);
	}

	public Interface reflect(Type type)
	{
		return this.typeBound.reflect(type);
	}

	public String toJVMType()
	{
		return this.typeBound.toJVMType();
	}

	public String toFullyQualifiedType()
	{
		return this.typeBound.toFullyQualifiedType();
	}

	public Result construct(Coordinate coordinate)
	{
		return this.typeBound.construct(coordinate);
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

	public boolean isAssignableTo(Type other)
	{
		return other.toString().equals(this.name);
	}

	public boolean hasParameter()
	{
		return true;
	}

	public String getSignature()
	{
		return this.name + ":" + this.typeBound.getSignature();
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

	public Type getTypeParameter(String typeName)
	{
		Type parameter = this.typeBound.getTypeParameter(typeName);

		if (parameter.toString().equals(this.name))
		{
			return this;
		}

		return parameter;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
