package yirgacheffe.compiler.assignment;

import yirgacheffe.lang.Array;

public class BranchAssignment implements Assignment
{
	private Assignment assignment;

	public BranchAssignment(Assignment assignment)
	{
		this.assignment = assignment;
	}

	public Assignment combineWith(Assignment other)
	{
		return new BranchAssignment(this.assignment.intersect(other));
	}

	public Assignment intersect(Assignment other)
	{
		return new BranchAssignment(this.assignment.intersect(other));
	}

	public Assignment intersect(Array<String> fields)
	{
		return new BranchAssignment(this.assignment.intersect(fields));
	}

	public boolean contains(String field)
	{
		return this.assignment.contains(field);
	}
}
