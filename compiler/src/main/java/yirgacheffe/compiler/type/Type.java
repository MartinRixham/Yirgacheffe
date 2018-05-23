package yirgacheffe.compiler.type;

public interface Type
{
	Class<?> reflectionClass();

	String toJVMType();

	String toFullyQualifiedType();

	int width();

	int getReturnInstruction();

	int getStoreInstruction();

	int getLoadInstruction();

	boolean isAssignableTo(Type other);

	boolean hasTypeParameter(Class<?> genericParameterType);
}
