package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public interface Statement
{
	boolean returns();

	Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller);

	Expression getFirstOperand();

	Array<VariableRead> getVariableReads();

	Array<VariableWrite> getVariableWrites();

	Expression getExpression();

	boolean isEmpty();
}
