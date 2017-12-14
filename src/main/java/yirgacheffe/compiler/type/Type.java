package yirgacheffe.compiler.type;

public interface Type
{
	String toJVMType();

	String toFullyQualifiedType();

	int width();
}
