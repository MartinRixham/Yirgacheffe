package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.expression.Expression;

public class FieldWrite implements Statement
{
	private String name;

	private Expression owner;

	private Expression value;

	public FieldWrite(String name, Expression owner, Expression value)
	{
		this.name = name;
		this.owner = owner;
		this.value = value;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		this.owner.compile(methodVisitor);
		this.value.compile(methodVisitor);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.owner.getType().toFullyQualifiedType(),
			this.name,
			this.value.getType().toJVMType());

	}
}
