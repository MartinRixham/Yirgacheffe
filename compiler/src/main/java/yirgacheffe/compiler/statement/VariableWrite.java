package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class VariableWrite implements Statement
{
	private String name;

	private Expression expression;

	private Coordinate coordinate;

	public VariableWrite(
		Coordinate coordinate,
		String name,
		Expression expression)
	{
		this.name = name;
		this.expression = expression;
		this.coordinate = coordinate;
	}

	public boolean returns()
	{
		return false;
	}

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		Type type = this.expression.getType(variables);
		Variable variable = variables.getVariable(this.name);
		Type variableType = variable.getType();
		Array<Error> errors = new Array<>();

		errors.push(this.expression.compile(methodVisitor, variables));

		methodVisitor.visitVarInsn(type.getStoreInstruction(), variable.getIndex());

		if (!type.isAssignableTo(variableType))
		{
			String message =
				"Cannot assign expression of type " +
				type + " to variable of type " +
				variableType + ".";

			errors.push(new Error(this.coordinate, message));
		}

		variables.write(this);

		return errors;
	}

	public Expression getFirstOperand()
	{
		return this.expression.getFirstOperand();
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
