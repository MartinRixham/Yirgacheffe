package yirgacheffe.compiler.statement;

import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.assignment.FieldAssignment;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Delegate;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.implementation.Implementation;
import yirgacheffe.compiler.implementation.NullImplementation;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

import java.util.Map;

public class VariableWrite implements Statement
{
	private Statement declaration;

	private String name;

	private Expression expression;

	private Coordinate coordinate;

	public VariableWrite(
		Coordinate coordinate,
		String name,
		Expression expression)
	{
		this.coordinate = coordinate;
		this.declaration = new DoNothing();
		this.name = name;
		this.expression = expression;
	}

	public VariableWrite(
		Coordinate coordinate,
		VariableDeclaration declaration,
		Expression expression)
	{
		this.coordinate = coordinate;
		this.declaration = declaration;
		this.name = declaration.getName();
		this.expression = expression;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Type type = this.expression.getType(variables);

		Result result = new Result()
			.concat(this.declaration.compile(variables, caller));

		Variable variable = variables.getVariable(this.name);
		Type variableType = variable.getType();

		result = result
			.concat(this.expression.compile(variables))
			.add(new VarInsnNode(
				type.getStoreInstruction(),
				variable.getIndex()));

		if (!type.isAssignableTo(variableType))
		{
			String message =
				"Cannot assign expression of type " +
				type + " to variable of type " +
				variableType + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		variables.write(this);
		variables.stackPop();

		return result;
	}

	public Expression getExpression()
	{
		return this.expression;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>(this);
	}

	public FieldAssignment getFieldAssignments()
	{
		return new FieldAssignment(new Array<>());
	}

	public Implementation getDelegatedInterfaces(
		Map<Delegate, Type> delegateTypes,
		Type thisType)
	{
		return new NullImplementation();
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}

	public String getName()
	{
		return this.name;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof String)
		{
			return this.name.equals(other);
		}

		if (other instanceof VariableWrite || other instanceof VariableRead)
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

	public boolean isEmpty()
	{
		return false;
	}
}
