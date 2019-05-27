package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.type.Type;

public interface Comparator
{
	void compile(MethodVisitor methodVisitor, Label label, Type type);
}
