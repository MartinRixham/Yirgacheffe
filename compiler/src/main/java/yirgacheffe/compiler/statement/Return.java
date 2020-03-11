package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.BlockFieldAssignment;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class Return implements Statement
{
	private Coordinate coordinate;

	private Type type;

	private Expression expression = new Nothing();

	public Return(Coordinate coordinate, Type type, Expression expression)
	{
		this.coordinate = coordinate;
		this.type = type;
		this.expression = expression;
	}

	public Return(Coordinate coordinate, Type type)
	{
		this.coordinate = coordinate;
		this.type = type;
	}

	public boolean returns()
	{
		return true;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Result result = new Result();
		Type type = this.expression.getType(variables);

		if (type.isAssignableTo(this.type))
		{
			result = result
				.concat(this.expression.compile(variables))
				.concat(type.convertTo(this.type))
				.add(new InsnNode(this.type.getReturnInstruction()));

			variables.stackPop();
		}
		else if (type.isAssignableTo(new ReferenceType(Throwable.class)))
		{
			result = result
				.concat(this.expression.compile(variables))
				.add(new InsnNode(Opcodes.ATHROW));
		}
		else
		{
			String message =
				"Mismatched return type: Cannot return expression of type " +
				type + " from method of return type " +
				this.type + ".";

			result = result
				.add(new InsnNode(this.type.getZero()))
				.add(new InsnNode(this.type.getReturnInstruction()))
				.add(new Error(this.coordinate, message));
		}

		return  result;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public FieldAssignment getFieldAssignments()
	{
		return new BlockFieldAssignment(new Array<>());
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return new NullImplementation();
	}

	public Expression getExpression()
	{
		return this.expression;
	}

	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public boolean equals(Object other)
	{
		return this.expression.equals(other);
	}

	@Override
	public int hashCode()
	{
		return this.expression.hashCode();
	}
}
