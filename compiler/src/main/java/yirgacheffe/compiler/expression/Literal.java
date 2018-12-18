package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class Literal implements Expression
{
	private Type type;

	private String text;

	public Literal(Type type, String text)
	{
		this.type = type;
		this.text = text;
	}

	public Type getType(Variables variables)
	{
		return this.type;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		if (this.type instanceof ReferenceType)
		{
			methodVisitor.visitLdcInsn(this.text.substring(1, this.text.length() - 1));
		}
		else if (this.type == PrimitiveType.BOOLEAN)
		{
			if (this.text.equals("true"))
			{
				methodVisitor.visitInsn(Opcodes.ICONST_1);
			}
			else
			{
				methodVisitor.visitInsn(Opcodes.ICONST_0);
			}
		}
		else if (this.type == PrimitiveType.CHAR)
		{
			methodVisitor.visitLdcInsn(this.text.charAt(1));
		}
		else
		{
			Double dub = new Double(this.text);

			if (dub.doubleValue() == 0)
			{
				methodVisitor.visitInsn(Opcodes.DCONST_0);
			}
			else if (dub.doubleValue() == 1)
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

	public Expression getFirstOperand()
	{
		return this;
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}
}
