package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public interface Statement
{
	boolean returns();

	Result compile(Variables variables, Signature caller);

	Array<VariableRead> getVariableReads();

	Array<VariableWrite> getVariableWrites();

	Array<String> getFieldAssignments();

	Expression getExpression();

	boolean isEmpty();
}
