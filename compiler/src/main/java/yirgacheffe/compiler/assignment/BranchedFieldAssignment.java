package yirgacheffe.compiler.assignment;

import yirgacheffe.lang.Array;

public class BranchedFieldAssignment implements FieldAssignment
{
	private Array<String> fields;

	private FieldAssignment branch;

	public BranchedFieldAssignment(Array<String> fields, FieldAssignment branch)
	{
		this.fields = fields;
		this.branch = branch;
	}

	public FieldAssignment combineWith(FieldAssignment other)
	{
		return other.combineWith(this.fields, this.branch);
	}

	public BranchedFieldAssignment combineWith(Array<String> fields)
	{
		return new BranchedFieldAssignment(
			this.fields.concat(fields),
			this.branch.combineWith(fields));
	}

	public BranchedFieldAssignment combineWith(
		Array<String> fields,
		FieldAssignment branch)
	{
		return new BranchedFieldAssignment(
			this.fields.concat(fields),
			this.branch.intersect(branch.combineWith(fields)));
	}

	public BranchedFieldAssignment intersect(FieldAssignment other)
	{
		return other.intersect(this.fields, this.branch);
	}

	public Array<String> intersect(Array<String> others)
	{
		Array<String> intersection = new Array<>();

		for (String field: this.fields)
		{
			if (others.contains(field))
			{
				intersection.push(field);
			}
		}

		return intersection;
	}

	public BranchedFieldAssignment intersect(
		Array<String> others,
		FieldAssignment branch)
	{
		return new BranchedFieldAssignment(
			this.intersect(others),
			branch.intersect(this.branch));
	}

	public boolean contains(String field)
	{
		return this.fields.contains(field) && this.branch.contains(field);
	}
}
