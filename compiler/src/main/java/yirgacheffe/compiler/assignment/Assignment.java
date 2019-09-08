package yirgacheffe.compiler.assignment;

import yirgacheffe.lang.Array;

public interface Assignment
{
	Assignment combineWith(Assignment other);

	Assignment intersect(Assignment other);

	Assignment intersect(Array<String> fields);

	boolean contains(String field);
}
