package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Type;

public interface Expression
{
	void compile(MethodVisitor methodVisitor);

	Type getType();
}
