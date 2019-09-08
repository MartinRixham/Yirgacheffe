package yirgacheffe.compiler.assignment;

import yirgacheffe.lang.Array;

public class FieldAssignment implements Assignment
{
	private Array<String> fields;

	public FieldAssignment(Array<String> fields)
	{
		this.fields = fields;
	}

	public Assignment combineWith(Assignment other)
	{
		if (other instanceof FieldAssignment)
		{
			FieldAssignment otherAssignment = (FieldAssignment) other;

			return new FieldAssignment(this.fields.concat(otherAssignment.fields));
		}
		else
		{
			return other.combineWith(this);
		}
	}

	public Assignment intersect(Assignment other)
	{
		return other.intersect(this.fields);
	}

	public Assignment intersect(Array<String> fields)
	{
		Array<String> assignments = new Array<>();

		for (String assignment: fields)
		{
			if (this.fields.contains(assignment))
			{
				assignments.push(assignment);
			}
		}

		return new FieldAssignment(assignments);
	}

	public boolean contains(String field)
	{
		return this.fields.contains("this") || this.fields.contains(field);
	}
}
