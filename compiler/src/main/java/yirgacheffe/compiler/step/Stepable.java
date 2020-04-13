package yirgacheffe.compiler.step;

import yirgacheffe.compiler.Result;

public interface Stepable
{
	Result convertType();

	Result duplicate();

	Result stepOne(boolean increment);

	Result stepOne(int index, boolean increment);

	Result store(int index);
}
