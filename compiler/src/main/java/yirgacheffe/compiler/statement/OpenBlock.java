package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;

public class OpenBlock implements Statement
{
	@Override
	public boolean compile(MethodVisitor methodVisitor, StatementResult result)
	{
		return false;
	}
}
