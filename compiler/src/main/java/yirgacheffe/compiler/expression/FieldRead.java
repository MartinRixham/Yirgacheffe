package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class FieldRead implements Expression
{
	private Coordinate coordinate;

	private Expression owner;

	private String name;

	private Type type;

	public FieldRead(Coordinate coordinate, Expression owner, String name, Type type)
	{
		this.coordinate = coordinate;
		this.owner = owner;
		this.name = name;
		this.type = type;
	}

	public Type getType(Variables variables)
	{
		return this.type;
	}

	public Result compile(Variables variables)
	{
		return this.owner.compile(variables)
			.concat(this.coordinate.compile())
			.add(new FieldInsnNode(
				Opcodes.GETFIELD,
				this.owner.getType(variables).toFullyQualifiedType(),
				this.name,
				this.type.toJVMType()));

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
}
