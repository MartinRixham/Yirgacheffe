package yirgacheffe.compiler.statement;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.BlockFieldAssignment;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class ParameterDeclaration implements Statement
{
	private Coordinate coordinate;

	private String name;

	private Type type;

	public ParameterDeclaration(Coordinate coordinate, String name, Type type)
	{
		this.coordinate = coordinate;
		this.name = name;
		this.type = type;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		variables.declare(this.name, this.type);

		if (this.type.equals(PrimitiveType.VOID))
		{
			String message = "Cannot declare parameter of type Void.";

			return new Result().add(new Error(this.coordinate, message));
		}

		return new Result();
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
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
		return new VariableRead(this.coordinate, this.name);
	}

	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof String)
		{
			return this.name.equals(other);
		}

		if (other instanceof ParameterDeclaration || other instanceof VariableRead)
		{
			return other.equals(this.name);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}
}
