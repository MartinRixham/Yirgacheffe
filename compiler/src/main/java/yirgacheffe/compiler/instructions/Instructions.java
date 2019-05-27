package yirgacheffe.compiler.instructions;

import yirgacheffe.compiler.type.PrimitiveType;

public interface Instructions
{
	int getReturn();

	int getStore();

	int getLoad();

	int convertTo(PrimitiveType type);

	int getZero();
}
