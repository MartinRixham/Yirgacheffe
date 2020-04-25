package yirgacheffe.compiler.function;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public interface Signature
{
	boolean isImplementedBy(Signature signature);

	String getDescriptor();

	String getSignature();

	Array<Type> getParameters();

	String getName();

	Type getReturnType();

	Label getLabel();
}
