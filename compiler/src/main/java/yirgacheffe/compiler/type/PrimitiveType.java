package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.member.ClassInterface;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.instructions.DoubleInstructions;
import yirgacheffe.compiler.instructions.FloatInstructions;
import yirgacheffe.compiler.instructions.Instructions;
import yirgacheffe.compiler.instructions.IntegerInstructions;
import yirgacheffe.compiler.instructions.LongIntegerInstructions;
import yirgacheffe.compiler.instructions.VoidInstructions;
import yirgacheffe.compiler.operator.BooleanOperator;

public enum PrimitiveType implements Type
{
	VOID(
		"Void", "V", 0, new VoidInstructions(),
		java.lang.Void.class, Float.NaN),

	BOOLEAN(
		"Bool", "Z", 1, new IntegerInstructions(),
		java.lang.Boolean.class, Float.NaN),

	BYTE("Num", "B", 1, new IntegerInstructions(),
		java.lang.Byte.class, Float.NaN),

	CHAR(
		"Char", "C", 1, new IntegerInstructions(),
		java.lang.Character.class, 2),

	INT(
		"Num", "I", 1, new IntegerInstructions(),
		java.lang.Integer.class, 3),

	LONG(
		"Num", "J", 2, new LongIntegerInstructions(),
		java.lang.Long.class, 4),

	FLOAT(
		"Num", "F", 2, new FloatInstructions(),
		java.lang.Float.class, 5),

	DOUBLE(
		"Num", "D", 2, new DoubleInstructions(),
		java.lang.Double.class, 6);

	private String name;

	private Class<?> reflectionClass;

	private String jvmType;

	private int width;

	private Instructions instructions;

	private float order;

	PrimitiveType(
		String name,
		String jvmType,
		int width,
		Instructions instructions,
		Class<?> reflectionClass,
		float order)
	{
		this.name = name;
		this.jvmType = jvmType;
		this.width = width;
		this.instructions = instructions;
		this.reflectionClass = reflectionClass;
		this.order = order;
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
		return this.reflectionClass.getName().replace('.', '/');
	}

	public Result construct(Coordinate coordinate)
	{
		return new Result();
	}

	public int width()
	{
		return this.width;
	}

	public int getReturnInstruction()
	{
		return this.instructions.getReturn();
	}

	public int getStoreInstruction()
	{
		return this.instructions.getStore();
	}

	public int getArrayStoreInstruction()
	{
		return this.instructions.getArrayStore();
	}

	public int getLoadInstruction()
	{
		return this.instructions.getLoad();
	}

	public int getZero()
	{
		return this.instructions.getZero();
	}

	public String toString()
	{
		return this.name;
	}

	public boolean isAssignableTo(Type other)
	{
		if (this == other)
		{
			return true;
		}
		else if (other instanceof PrimitiveType)
		{
			PrimitiveType otherPrimitive = (PrimitiveType) other;

			return this.order > 0 && otherPrimitive.order > 0;
		}
		else
		{
			return other.reflect().isImplementedBy(this.reflectionClass);
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
		return new String[0];
	}

	public boolean isPrimitive()
	{
		return true;
	}

	public Result newArray()
	{
		return new Result().add(
			new IntInsnNode(Opcodes.NEWARRAY, this.instructions.getType()));
	}

	public Result convertTo(Type type)
	{
		if (type instanceof GenericType)
		{
			String descriptor =
				"(" + this.toJVMType() + ")L" +
					this.toFullyQualifiedType() + ";";

			return new Result().add(
				new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					this.toFullyQualifiedType(),
					"valueOf",
					descriptor,
					false));
		}
		else if (type.equals(this))
		{
			return new Result();
		}
		else if (type.isPrimitive())
		{
			return new Result().add(
				new InsnNode(this.instructions.convertTo(type)));
		}
		else
		{
			String descriptor =
				"(" + this.toJVMType() + ")L" +
					this.toFullyQualifiedType() + ";";

			return new Result().add(
				new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					this.toFullyQualifiedType(),
					"valueOf",
					descriptor,
					false));
		}
	}

	public Result swapWith(Type type)
	{
		if (this.width == 2)
		{
			if (type.width() == 1)
			{
				return new Result()
					.add(new InsnNode(Opcodes.DUP_X2))
					.add(new InsnNode(Opcodes.POP));
			}
			else if (type.width() == 2)
			{
				return new Result()
					.add(new InsnNode(Opcodes.DUP2_X2))
					.add(new InsnNode(Opcodes.POP2));
			}
		}
		else if (this.width == 1)
		{
			if (type.width() == 1)
			{
				return new Result()
					.add(new InsnNode(Opcodes.SWAP));
			}
			else if (type.width() == 2)
			{
				return new Result()
					.add(new InsnNode(Opcodes.DUP2_X1))
					.add(new InsnNode(Opcodes.POP2));
			}
		}

		return new Result();
	}

	public Type intersect(Type type)
	{
		if (type.equals(this))
		{
			return this;
		}
		else if (type instanceof PrimitiveType)
		{
			PrimitiveType primitiveType = (PrimitiveType) type;

			if (this.order >= primitiveType.order)
			{
				return this;
			}
			else if (primitiveType.order > this.order)
			{
				return primitiveType;
			}
			else
			{
				return new ReferenceType(Object.class);
			}
		}
		else
		{
			return type.intersect(this);
		}
	}

	public Result compare(BooleanOperator operator, Label label)
	{
		if (this.equals(PrimitiveType.DOUBLE))
		{
			return new Result()
				.add(new InsnNode(Opcodes.DCONST_1))
				.add(new InsnNode(Opcodes.DCMPL))
				.add(new InsnNode(Opcodes.ICONST_1))
				.add(new InsnNode(Opcodes.IADD))
				.add(new JumpInsnNode(operator.integerOpcode(), new LabelNode(label)));
		}
		if (this.equals(PrimitiveType.LONG))
		{
			return new Result()
				.add(new InsnNode(Opcodes.LCONST_0))
				.add(new InsnNode(Opcodes.LCMP))
				.add(new JumpInsnNode(operator.integerOpcode(), new LabelNode(label)));
		}
		else
		{
			return new Result()
				.add(new JumpInsnNode(
					operator.integerOpcode(),
					new LabelNode(label)));
		}
	}

	public Result attempt()
	{
		return new Result();
	}

	public Type getTypeParameter(String typeName)
	{
		return new NullType();
	}
}
