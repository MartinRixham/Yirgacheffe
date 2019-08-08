package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;

public class AttemptedType implements Type
{
	private Type type;

	public AttemptedType(Type type)
	{
		this.type = type;
	}

	public Class<?> reflectionClass()
	{
		return Object.class;
	}

	public String toJVMType()
	{
		return "Ljava/lang/Object;";
	}

	public String toFullyQualifiedType()
	{
		return "java/lang/Object";
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
		return this.type.isAssignableTo(other);
	}

	public boolean hasParameter()
	{
		return false;
	}

	public String getSignature()
	{
		return null;
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
		Result result = new Result();

		if (type.isPrimitive())
		{
			result = result
				.add(new TypeInsnNode(
					Opcodes.CHECKCAST,
					this.type.toFullyQualifiedType()))
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Boxer",
					"ofValue",
					"(L" + this.type.toFullyQualifiedType() + ";)" +
						this.type.getSignature(),
					false));
		}

		return result;
	}

	public Result swapWith(Type type)
	{
		return new Result();
	}

	public Type intersect(Type type)
	{
		return this.type.intersect(type);
	}

	public Result compare(BooleanOperator operator, Label label)
	{
		return new Result();
	}
}