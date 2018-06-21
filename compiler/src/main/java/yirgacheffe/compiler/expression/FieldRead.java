package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FieldRead implements Expression
{
	private String owner;

	private String name;

	private String descriptor;

	public FieldRead(String owner, String name, String descriptor)
	{
		this.owner = owner;
		this.name = name;
		this.descriptor = descriptor;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitFieldInsn(
			Opcodes.GETFIELD,
			this.owner,
			this.name,
			this.descriptor);
	}
}
