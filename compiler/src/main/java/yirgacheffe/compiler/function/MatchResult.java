package yirgacheffe.compiler.function;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.MismatchedTypes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public interface MatchResult
{
	MatchResult betterOf(MatchResult other);

	int score();

	Result compileArguments(Variables variables);

	String getDescriptor();

	String getName();

	Type getReturnType();

	Array<MismatchedTypes> getMismatchedParameters();
}
