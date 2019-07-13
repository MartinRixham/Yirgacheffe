package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;

public interface Type
{
	Class<?> reflectionClass();

	String toJVMType();

	String toFullyQualifiedType();

	int width();

	int getReturnInstruction();

	int getStoreInstruction();

	int getArrayStoreInstruction();

	int getLoadInstruction();

	int getZero();

	boolean isAssignableTo(Type other);

	boolean hasParameter();

	String getSignature();

	boolean isPrimitive();

	Result newArray();

	Result convertTo(Type type);

	Result swapWith(Type type);

	Type intersect(Type type);

	Result compare(BooleanOperator operator, Label label);
}
