package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;

public class ReferenceType implements Type
{
	private Class<?> reflectionClass;

	public ReferenceType(Class<?> reflectionClass)
	{
		this.reflectionClass = reflectionClass;
	}

	public Class<?> reflectionClass()
	{
		return this.reflectionClass;
	}

	public String toJVMType()
	{
		return "L" + this.toFullyQualifiedType() + ";";
	}

	public String toFullyQualifiedType()
	{
		return this.reflectionClass.getName().replace('.', '/');
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
		return this.reflectionClass.getName();
	}

	public boolean isAssignableTo(Type other)
	{
		return other.reflectionClass().isAssignableFrom(this.reflectionClass());
	}

	public boolean hasParameter()
	{
		return false;
	}

	public String getSignature()
	{
		return this.toJVMType();
	}

	public boolean isPrimitive()
	{
		return false;
	}

	public Result newArray()
	{
		return new Result().add(
			new TypeInsnNode(Opcodes.ANEWARRAY, this.toFullyQualifiedType()));
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
		return new IntersectionType(this, type);
	}

	public Result compare(BooleanOperator operator, Label label)
	{
		if (this.isAssignableTo(new ReferenceType(String.class)))
		{
			return new Result()
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Falsyfier",
					"isTruthy",
					"(Ljava/lang/String;)Z",
					false))
				.add(new JumpInsnNode(
					operator.integerOpcode(),
					new LabelNode(label)));
		}
		else
		{
			return new Result()
				.add(new JumpInsnNode(
					operator.referenceOpcode(),
					new LabelNode(label)));
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ReferenceType)
		{
			ReferenceType referenceType = (ReferenceType) other;

			return this.reflectionClass.equals(referenceType.reflectionClass);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return this.reflectionClass.hashCode();
	}
}
