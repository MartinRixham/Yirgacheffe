package yirgacheffe.compiler.member;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Type;

public interface Property
{
	String getName();

	Result checkType(Coordinate coordinate, Type type);

	boolean isStatic();

	Type getType();
}
