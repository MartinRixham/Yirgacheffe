package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.Type;

public class FieldRead implements Expression
{
	private Expression owner;

	private String name;

	private Type type;

	public FieldRead(Expression owner, String name, Type type)
	{
		this.owner = owner;
		this.name = name;
		this.type = type;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		this.owner.compile(methodVisitor);

		methodVisitor.visitFieldInsn(
			Opcodes.GETFIELD,
			this.owner.getType().toFullyQualifiedType().replace(".", "/"),
			this.name,
			this.type.toJVMType());
	}

	@Override
	public Type getType()
	{
		return this.type;
	}
}
