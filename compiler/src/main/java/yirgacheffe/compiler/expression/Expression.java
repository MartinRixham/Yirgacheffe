package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public interface Expression
{
	Type getType(Variables variables);

	Result compile(Variables variables);

	Result compileCondition(Variables variables, Label trueLabel, Label falseLabel);

	boolean isCondition(Variables variables);

	Array<VariableRead> getVariableReads();

	Coordinate getCoordinate();
}
