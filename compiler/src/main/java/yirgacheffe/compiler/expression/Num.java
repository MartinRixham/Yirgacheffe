package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
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

	private Type getType()
	{
		for (char character: this.text.toCharArray())
		{
			if (!Character.isDigit(character))
			{
				return PrimitiveType.DOUBLE;
			}
		}

		return PrimitiveType.INT;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		if (this.getType(variables) == PrimitiveType.INT)
		{
			Integer integer = Integer.valueOf(this.text);

			if (integer == 0)
			{
				methodVisitor.visitInsn(Opcodes.ICONST_0);
			}
			else if (integer == 1)
			{
				methodVisitor.visitInsn(Opcodes.ICONST_1);
			}
			else
			{
				methodVisitor.visitLdcInsn(integer);
			}
		}
		else
		{
			Double dub = Double.valueOf(this.text);

			if (dub == 0)
			{
				methodVisitor.visitInsn(Opcodes.DCONST_0);
			}
			else if (dub == 1)
			{
				methodVisitor.visitInsn(Opcodes.DCONST_1);
			}
			else
			{
				methodVisitor.visitLdcInsn(dub);
			}
		}

		return new Array<>();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Object getValue()
	{
		if (this.getType() == PrimitiveType.INT)
		{
			return Integer.valueOf(this.text);
		}
		else
		{
			return Double.valueOf(this.text);
		}
	}
}
