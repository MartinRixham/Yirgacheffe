package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.Type;

public interface Expression
{
	Type check(Variables result);

	ExpressionResult compile(MethodVisitor methodVisitor);
}
