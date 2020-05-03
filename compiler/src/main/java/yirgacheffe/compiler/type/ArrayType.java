package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

import java.util.Collection;

public class ArrayType implements Type
{
	private ParameterisedType type;

	public ArrayType(ParameterisedType type)
	{
		this.type = type;
	}

	public Interface reflect()
	{
		return this.type.reflect();
	}

	public Interface reflect(Type type)
	{
		return this.type.reflect(type);
	}

	public String toJVMType()
	{
		return this.type.toJVMType();
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

	public boolean isAssignableTo(Type other)
	{
		Type parameter = this.type.getTypeParameter("T");

		Type collection =
			new ParameterisedType(
				new ReferenceType(Collection.class),
				new Array<>(parameter));

		return this.type.isAssignableTo(other) ||
			collection.isAssignableTo(other);
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
		Type parameter = this.type.getTypeParameter("T");

		Type collection =
			new ParameterisedType(
				new ReferenceType(Collection.class),
				new Array<>(parameter));

		if (collection.equals(type))
		{
			return new Result()
				.add(new MethodInsnNode(
					Opcodes.INVOKEVIRTUAL,
					"yirgacheffe/lang/Array",
					"toArray",
					"()[Ljava/lang/Object;",
					false))
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"java/util/Arrays",
					"asList",
					"([Ljava/lang/Object;)Ljava/util/List;",
					false));
		}
		else
		{
			return this.type.convertTo(type);
		}
	}

	public Result swapWith(Type type)
	{
		return this.type.swapWith(type);
	}

	public Type intersect(Type type)
	{
		return this.type.intersect(type);
	}

	public Result compare(BooleanOperator operator, Label label)
	{
		return this.type.compare(operator, label);
	}

	public Type getTypeParameter(String typeName)
	{
		return this.type.getTypeParameter(typeName);
	}

	@Override
	public String toString()
	{
		return this.type.toString();
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
