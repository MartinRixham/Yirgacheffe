package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public interface Expression
{
	Type getType(Variables variables);

	Array<Error> compile(MethodVisitor methodVisitor, Variables variables);

	Array<Error> compileCondition(
		MethodVisitor methodVisitor,
		Variables variables,
		Label trueLabel,
		Label falseLabel);

	boolean isCondition(Variables variables);

	Array<VariableRead> getVariableReads();
}
