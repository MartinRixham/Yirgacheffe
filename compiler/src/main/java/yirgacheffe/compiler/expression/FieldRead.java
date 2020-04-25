package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.member.Property;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class FieldRead implements Expression
{
	private Coordinate coordinate;

	private Expression owner;

	private String name;

	public FieldRead(Coordinate coordinate, Expression owner, String name)
	{
		this.coordinate = coordinate;
		this.owner = owner;
		this.name = name;
	}

	public Type getType(Variables variables)
	{
		Type ownerType = this.owner.getType(variables);
		Property field = ownerType.reflect().getField(this.name);

		return field.getType();
	}

	public Result compile(Variables variables)
	{
		Result result = new Result();
		Type ownerType = this.owner.getType(variables);
		Type type = this.getType(variables);

		if (type instanceof NullType)
		{
			String message = "Unknown field '" + this.name + "'.";

			result = result.add(new Error(this.coordinate, message));
		}

		result = result
			.concat(this.owner.compile(variables))
			.concat(this.coordinate.compile())
			.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				ownerType.toFullyQualifiedType(),
				this.name,
				type.toJVMType()));

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.owner.getVariableReads();
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}
}
