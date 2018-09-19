package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;

public interface Statement
{
	boolean compile(MethodVisitor methodVisitor, StatementResult statementResult);
}
