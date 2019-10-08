package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Num implements Expression, Literal
{
	private String text;

	public Num(String text)
	{
		this.text = text;
	}

	public Type getType(Variables variables)
	{
		return this.getType();
	}

	public Type getType()
	{
		for (char character: this.text.toCharArray())
		{
			if (!Character.isDigit(character))
			{
				return PrimitiveType.DOUBLE;
			}
		}

		if (this.text.equals("0") || this.text.equals("1"))
		{
			return PrimitiveType.INT;
		}
		else
		{
			return PrimitiveType.LONG;
		}
	}

	public Result compile(Variables variables)
	{
		Type type = this.getType(variables);

		variables.stackPush(type);

		Result result = new Result();

		if (type.equals(PrimitiveType.INT))
		{
			Integer integer = Integer.valueOf(this.text);

			if (integer == 0)
			{
				result = result.add(new InsnNode(Opcodes.ICONST_0));
			}
			else if (integer == 1)
			{
				result = result.add(new InsnNode(Opcodes.ICONST_1));
			}
		}
		else if (type.equals(PrimitiveType.LONG))
		{
			Long longInteger = Long.valueOf(this.text);

			result = result.add(new LdcInsnNode(longInteger));
		}
		else
		{
			Double dub = Double.valueOf(this.text);

			if (dub == 0)
			{
				result = result.add(new InsnNode(Opcodes.DCONST_0));
			}
			else if (dub == 1)
			{
				result = result.add(new InsnNode(Opcodes.DCONST_1));
			}
			else
			{
				result = result.add(new LdcInsnNode(dub));
			}
		}

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Object getValue()
	{
		Type type = this.getType();

		if (type.equals(PrimitiveType.INT))
		{
			return Integer.valueOf(this.text);
		}
		else if (type.equals(PrimitiveType.LONG))
		{
			return Long.valueOf(this.text);
		}
		else
		{
			return Double.valueOf(this.text);
		}
	}
}
