package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.member.ClassInterface;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.operator.BooleanOperator;

public class VariableType implements Type
{
	private String name;

	public VariableType(String name)
	{
		this.name = name;
	}

	public Interface reflect()
	{
		return new ClassInterface(this, Object.class);
	}

	public Interface reflect(Type type)
	{
		return new ClassInterface(this, Object.class);
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
		return new Result();
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
		if (other instanceof VariableType)
		{
			VariableType otherType = (VariableType) other;

			return otherType.name.equals(this.name);
		}
		else
		{
			return false;
		}
	}

	public boolean hasParameter()
	{
		return true;
	}

	public String getSignature()
	{
		return "T" + this.name + ";";
	}

	public String[] getSignatureTypes()
	{
		return new String[0];
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

	public Result attempt()
	{
		return new Result();
	}

	public Type getTypeParameter(String typeName)
	{
		return new NullType();
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
