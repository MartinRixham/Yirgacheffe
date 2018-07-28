package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.statement.StatementResult;
import yirgacheffe.compiler.type.Type;

public interface Expression
{
	Type check(StatementResult result);

	void compile(MethodVisitor methodVisitor);
}
