package yirgacheffe.compiler.variables;

import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.lang.Array;

import java.util.Map;

public interface Variables
{
	Map<String, Variable> getVariables();

	void setVariables(Map<String, Variable> variables);

	void delegate(Delegate delegate, Type type);

	void declare(String name, Type type);

	Variable redeclare(String name);

	void read(VariableRead variableRead);

	void write(VariableWrite variableWrite);

	void setNumberType(String name, Type type);

	Variable getVariable(String name);

	void optimise(Expression variableRead, Expression writtenExpression);

	boolean canOptimise(Expression variableRead);

	Expression getOptimisedExpression(Expression variableRead);

	boolean hasConstant(String name);

	Object getConstant(String name);

	int nextVariableIndex();

	Array<Type> getStack();

	void stackPush(Type type);

	void stackPop();
}
