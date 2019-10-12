package yirgacheffe.compiler.expression;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public interface Parameterisable
{
	Result compileArguments(Variables variables);

	Array<Type> getParameters(Variables variables);
}
