package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Type;

public interface Comparator
{
	Result compile(Label label, Type type);
}
