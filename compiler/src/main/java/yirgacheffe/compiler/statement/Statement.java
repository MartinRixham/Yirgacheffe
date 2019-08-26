package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public interface Statement
{
	boolean returns();

	Result compile(Variables variables, Signature caller);

	Array<VariableRead> getVariableReads();

	Array<VariableWrite> getVariableWrites();

	Array<String> getFieldAssignments();

	Array<Type> getDelegatedInterfaces(Map<Delegate, Type> delegateTypes, Type thisType);

	Expression getExpression();

	boolean isEmpty();
}
