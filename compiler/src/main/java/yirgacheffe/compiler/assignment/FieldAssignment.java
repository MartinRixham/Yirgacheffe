package yirgacheffe.compiler.assignment;

import yirgacheffe.lang.Array;

public class FieldAssignment
{
	private Array<String> fields;

	private FieldAssignment branch;

	public FieldAssignment(Array<String> fields)
	{
		this.fields = fields;
	}

	public FieldAssignment(FieldAssignment branch)
	{
		this.fields = new Array<>();
		this.branch = branch;
	}

	private FieldAssignment(Array<String> fields, FieldAssignment branch)
	{
		this.fields = fields;
		this.branch = branch;
	}

	public FieldAssignment combineWith(FieldAssignment other)
	{
		Array<String> fields = this.fields.concat(other.fields);

		FieldAssignment otherBranch = null;

		if (other.branch != null)
		{
			otherBranch = other.branch.combineWith(this.fields);
		}

		FieldAssignment branch;

		if (this.branch == null)
		{
			branch = otherBranch;
		}
		else if (otherBranch == null)
		{
			branch = this.branch;
		}
		else
		{
			branch = this.branch.intersect(otherBranch);
		}

		return new FieldAssignment(fields, branch);
	}

	private FieldAssignment combineWith(Array<String> fields)
	{
		if (this.branch == null)
		{
			return new FieldAssignment(this.fields.concat(fields));
		}
		else
		{
			return new FieldAssignment(
				this.fields.concat(fields),
				this.branch.combineWith(fields));
		}
	}

	public FieldAssignment intersect(FieldAssignment other)
	{
		if (this.branch != null && other.branch != null)
		{
			return new FieldAssignment(
				this.intersectWith(other.fields), other.branch.intersect(this.branch));
		}
		else if (this.branch == null)
		{
			return new FieldAssignment(this.intersectWith(other.fields), other.branch);
		}
		else
		{
			return new FieldAssignment(this.intersectWith(other.fields), this.branch);
		}
	}

	private Array<String> intersectWith(Array<String> others)
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

	public boolean contains(String field)
	{
		if (this.fields.contains("this"))
		{
			return true;
		}
		else if (this.branch == null)
		{
			return this.fields.contains(field);
		}
		else
		{
			return this.fields.contains(field) && this.branch.contains(field);
		}
	}
}
