package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public interface Condition
{
	Array<Error> compileCondition(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label);
}
