package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Variables;

public interface Comparison
{
	Result compile(Variables variables, Label label);
}
