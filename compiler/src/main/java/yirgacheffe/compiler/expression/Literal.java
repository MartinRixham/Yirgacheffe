package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.statement.StatementResult;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

public class Literal implements Expression
{
	private Type type;

	private String text;

	public Literal(Type type, String text)
	{
		this.type = type;
		this.text = text;
	}

	@Override
	public Type check(StatementResult result)
	{
		return this.type;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
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
	}
}
