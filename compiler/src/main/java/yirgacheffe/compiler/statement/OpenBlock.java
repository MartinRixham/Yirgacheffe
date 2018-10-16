package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Variables;

public class OpenBlock implements Statement
{
	@Override
	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		return new StatementResult(false);
	}
}
