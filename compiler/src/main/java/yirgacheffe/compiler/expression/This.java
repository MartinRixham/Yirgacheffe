package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.statement.StatementResult;
import yirgacheffe.compiler.type.Type;

public class This implements Expression
{
	private Type type;

	public This(Type type)
	{
		this.type = type;
	}

	@Override
	public Type check(StatementResult result)
	{
		return this.type;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
	}
}
