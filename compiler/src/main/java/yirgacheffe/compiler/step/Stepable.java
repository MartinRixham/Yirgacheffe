package yirgacheffe.compiler.step;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.type.Type;

public interface Stepable
{
	Result convertType();

	Result duplicate();

	Result stepOne(boolean increment);

	Result stepOne(int index, boolean increment);

	Result store(int index);

	Type getType();
}
