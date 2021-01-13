package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.operator.BooleanOperator;

public class AttemptedType implements Type
{
	private Type type;

	public AttemptedType(Type type)
	{
		this.type = type;
	}

	public Interface reflect()
	{
		return this.type.reflect(this);
	}

	public Interface reflect(Type type)
	{
		return this.type.reflect(type);
	}

	public String toJVMType()
	{
		return "Ljava/lang/Object;";
	}

	public String toFullyQualifiedType()
	{
		return "java/lang/Object";
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
		return this.type.getSignature();
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
		Result result = new Result();

		if (type.isPrimitive())
		{
			result = result
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Boxer",
					"to" + this.type.reflect().getSimpleName(),
					"(Ljava/lang/Object;)" +
						this.type.getSignature(),
					false))
				.concat(this.type.convertTo(type));
		}
		else
		{
			Label instanceLabel = new Label();
			Label jumpLabel = new Label();

			result = result
				.add(new InsnNode(Opcodes.DUP))
				.add(new TypeInsnNode(Opcodes.INSTANCEOF, type.toFullyQualifiedType()))
				.add(new JumpInsnNode(Opcodes.IFNE, new LabelNode(instanceLabel)))
				.add(new TypeInsnNode(Opcodes.CHECKCAST, type.toFullyQualifiedType()))
				.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(jumpLabel)))
				.add(new LabelNode(instanceLabel))
				.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Throwable"))
				.add(new InsnNode(Opcodes.ATHROW))
				.add(new LabelNode(jumpLabel));
		}

		return result;
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
		return this
			.convertTo(this.type)
			.concat(this.type.compare(operator, label));
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
}
