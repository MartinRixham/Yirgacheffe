package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public interface Statement
{
	boolean returns();

	StatementResult compile(MethodVisitor methodVisitor, Variables variables);

	Expression getFirstOperand();

	Array<VariableRead> getVariableReads();
}
