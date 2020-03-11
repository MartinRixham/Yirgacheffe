package yirgacheffe.compiler.assignment;

import yirgacheffe.lang.Array;

public class TotalFieldAssignment implements FieldAssignment
{
	public TotalFieldAssignment combineWith(FieldAssignment other)
	{
		return this;
	}

	public TotalFieldAssignment combineWith(Array<String> fields)
	{
		return this;
	}

	public TotalFieldAssignment combineWith(
		Array<String> fields,
		FieldAssignment branch)
	{
		return this;
	}

	public FieldAssignment intersect(FieldAssignment other)
	{
		return other;
	}

	public Array<String> intersect(Array<String> others)
	{
		return others;
	}

	public BranchedFieldAssignment intersect(Array<String> others, FieldAssignment branch)
	{
		return new BranchedFieldAssignment(others, branch);
	}

	public boolean contains(String field)
	{
		return true;
	}
}
