package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public interface Comparison
{
	Array<Error> compile(MethodVisitor methodVisitor, Variables variables, Label label);
}
