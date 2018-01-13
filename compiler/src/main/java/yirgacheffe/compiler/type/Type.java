package yirgacheffe.compiler.type;

public interface Type
{
	Class<?> reflectionClass();

	String toJVMType();

	String toFullyQualifiedType();

	int width();

	int getReturnInstruction();

	int getStoreInstruction();
}
