package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
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
	public void compile(MethodVisitor methodVisitor)
	{
		if (this.type instanceof ReferenceType)
		{
			methodVisitor.visitLdcInsn(this.text.substring(1, this.text.length() - 1));
		}
		else if (this.type == PrimitiveType.BOOLEAN)
		{
			methodVisitor.visitLdcInsn(this.text.equals("true"));
		}
		else if (this.type == PrimitiveType.CHAR)
		{
			methodVisitor.visitLdcInsn(this.text.charAt(1));
		}
		else
		{
			methodVisitor.visitLdcInsn(new Double(this.text));
		}
	}

	@Override
	public int getStackHeight()
	{
		if (this.type == PrimitiveType.DOUBLE)
		{
			return 2;
		}
		else
		{
			return 1;
		}
	}
}
