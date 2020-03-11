package yirgacheffe.compiler.assignment;

import yirgacheffe.lang.Array;

public interface FieldAssignment
{
	FieldAssignment combineWith(FieldAssignment other);

	FieldAssignment combineWith(Array<String> fields);

	BranchedFieldAssignment combineWith(Array<String> fields, FieldAssignment branch);

	FieldAssignment intersect(FieldAssignment other);

	Array<String> intersect(Array<String> others);

	BranchedFieldAssignment intersect(Array<String> others, FieldAssignment branch);

	boolean contains(String field);
}
