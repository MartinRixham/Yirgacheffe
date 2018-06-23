package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class New implements Expression
{
	private String owner;

	public New(String owner)
	{
		this.owner = owner;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitTypeInsn(Opcodes.NEW, this.owner);
		methodVisitor.visitInsn(Opcodes.DUP);
	}
}
