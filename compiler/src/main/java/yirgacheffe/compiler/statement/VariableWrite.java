package yirgacheffe.compiler.statement;

import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Try;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

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
			.concat(type.convertTo(variableType))
			.add(new VarInsnNode(
				variableType.getStoreInstruction(),
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

	@Override
	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>(this);
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
		if (this.expression instanceof Try)
		{
			return false;
		}

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
