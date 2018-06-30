package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Type;

public class Variable implements Expression
{
	private int index;

	private Type type;

	public Variable(int index, Type type)
	{
		this.index = index;
		this.type = type;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitVarInsn(this.type.getLoadInstruction(), this.index);
	}

	public int getIndex()
	{
		return this.index;
	}

	public Type getType()
	{
		return this.type;
	}
}
