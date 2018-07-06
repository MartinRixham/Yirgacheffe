package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;

public interface Statement
{
	void compile(MethodVisitor methodVisitor);
}
