package yirgacheffe.compiler.instructions;

import yirgacheffe.compiler.type.Type;

public interface Instructions
{
	int getReturn();

	int getStore();

	int getArrayStore();

	int getLoad();

	int convertTo(Type type);

	int getZero();

	int getType();
}
