package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;

public interface Expression
{
	void compile(MethodVisitor methodVisitor);
}
