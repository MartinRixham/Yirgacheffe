package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.Type;

public class FieldRead implements Expression
{
	private Type owner;

	private String name;

	private String descriptor;

	public FieldRead(Type owner, String name, String descriptor)
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
			this.owner.toFullyQualifiedType().replace(".", "/"),
			this.name,
			this.descriptor);
	}

	@Override
	public int getStackHeight()
	{
		return 1;
	}
}
