package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.member.ClassInterface;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

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

	public Interface reflect()
	{
		return new ClassInterface(this, this.reflectionClass);
	}

	public Interface reflect(Type type)
	{
		return new ClassInterface(type, this.reflectionClass);
	}

	public String toJVMType()
	{
		return this.jvmType;
	}

	public String toFullyQualifiedType()
	{
		return this.fullyQualifiedType;
	}

	public Result construct(Coordinate coordinate)
	{
		return this.type.construct(coordinate);
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
			ReferenceType arrayType = new ReferenceType(Array.class);
			Type type = new ParameterisedType(arrayType, new Array<>(this.type));

			return type.isAssignableTo(other);
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

	public String[] getSignatureTypes()
	{
		return this.type.getSignatureTypes();
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
		Type arrayType =
			new ParameterisedType(new ReferenceType(Array.class), new Array<>(this.type));

		if (arrayType.equals(type))
		{
			return new Result()
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Array",
					"fromArray",
					"([Ljava/lang/Object;)Lyirgacheffe/lang/Array;",
					false));
		}
		else
		{
			return new Result();
		}
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

	public Result attempt()
	{
		return this.type.attempt();
	}

	public Type getTypeParameter(String typeName)
	{
		return new NullType();
	}

	public Type getElementType()
	{
		return this.type;
	}

	@Override
	public boolean equals(Object other)
	{
		return other.equals(this.reflectionClass);
	}

	@Override
	public int hashCode()
	{
		return this.reflectionClass.hashCode();
	}
}
