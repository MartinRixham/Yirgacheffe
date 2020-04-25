package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.BlockFieldAssignment;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.member.Interface;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.member.Property;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class FieldWrite implements Statement
{
	private Coordinate coordinate;

	private String name;

	private Expression owner;

	private Expression value;

	public FieldWrite(
		Coordinate coordinate,
		String name,
		Expression owner,
		Expression value)
	{
		this.coordinate = coordinate;
		this.name = name;
		this.owner = owner;
		this.value = value;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Type ownerType = this.owner.getType(variables);
		Type type = this.value.getType(variables);
		Result result = new Result();
		Interface members = ownerType.reflect();
		Property field = members.getField(this.name);

		result = result
			.concat(field.checkType(this.coordinate, type))
			.concat(this.owner.compile(variables))
			.concat(this.value.compile(variables))
			.add(new FieldInsnNode(
				Opcodes.PUTFIELD,
				ownerType.toFullyQualifiedType(),
				this.name,
				type.toJVMType()));

		variables.stackPop();
		variables.stackPop();

		return result;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.owner.getVariableReads()
			.concat(this.value.getVariableReads());
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public FieldAssignment getFieldAssignments()
	{
		return new BlockFieldAssignment(new Array<>(this.name));
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return new NullImplementation();
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
