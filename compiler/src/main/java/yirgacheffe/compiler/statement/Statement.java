package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Variables;

public interface Statement
{
	boolean returns();

	StatementResult compile(MethodVisitor methodVisitor, Variables variables);
}
