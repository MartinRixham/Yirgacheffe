package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;

public class Literal implements Expression
{
	private Object value;

	public Literal(Object value)
	{
		this.value = value;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitLdcInsn(this.value);
	}
}
