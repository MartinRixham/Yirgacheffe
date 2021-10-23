package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Type;

public interface Declaration
{
	String getName();

	Type getType();

	Coordinate getCoordinate();
}
