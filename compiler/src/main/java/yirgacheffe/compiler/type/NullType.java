package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.member.NullInterface;
import yirgacheffe.compiler.operator.BooleanOperator;

public class NullType implements Type
{
	private String name;

	public NullType(String name)
	{
		this.name = name;
	}

	public NullType()
	{
		this.name = "java.lang.Object";
	}

	public Interface reflect()
	{
		return new NullInterface();
	}

	public Interface reflect(Type type)
	{
		return new NullInterface();
	}

	public String toJVMType()
	{
		return "Ljava/lang/Object;";
	}

	public String toFullyQualifiedType()
	{
		return this.name.replace(".", "/");
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
		return true;
	}

	public boolean hasParameter()
	{
		return false;
	}

	public String getSignature()
	{
		return this.toJVMType();
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
		return this;
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
