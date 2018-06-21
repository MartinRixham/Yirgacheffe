package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InvokeConstructor implements Expression
{
	private String owner;

	private String descriptor;

	public InvokeConstructor(String owner, String descriptor)
	{
		this.owner = owner;
		this.descriptor = descriptor;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			this.owner,
			"<init>",
			this.descriptor,
			false);
	}
}
