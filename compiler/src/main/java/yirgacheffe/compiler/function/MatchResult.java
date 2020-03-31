package yirgacheffe.compiler.function;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public interface MatchResult
{
	MatchResult betterOf(MatchResult other);

	boolean betters(int score);

	Result compileArguments(Variables variables);

	String getName();

	Array<Type> getParameterTypes();

	Type getReturnType();
}
