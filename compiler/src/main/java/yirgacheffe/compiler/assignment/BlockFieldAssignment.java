package yirgacheffe.compiler.assignment;

import yirgacheffe.lang.Array;

public class BlockFieldAssignment implements FieldAssignment
{
	private Array<String> fields;

	public BlockFieldAssignment(Array<String> fields)
	{
		this.fields = fields;
	}

	public FieldAssignment combineWith(FieldAssignment other)
	{
		return other.combineWith(this.fields);
	}

	public BlockFieldAssignment combineWith(Array<String> fields)
	{
		return new BlockFieldAssignment(this.fields.concat(fields));
	}

	public BranchedFieldAssignment combineWith(
		Array<String> fields,
		FieldAssignment branch)
	{
		return new BranchedFieldAssignment(this.fields.concat(fields), branch);
	}

	public FieldAssignment intersect(FieldAssignment other)
	{
		return new BlockFieldAssignment(other.intersect(this.fields));
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

	public BranchedFieldAssignment intersect(Array<String> others, FieldAssignment branch)
	{
		return new BranchedFieldAssignment(this.intersect(others), branch);
	}

	public boolean contains(String field)
	{
		if (this.fields.contains("this"))
		{
			return true;
		}
		else
		{
			return this.fields.contains(field);
		}
	}
}
