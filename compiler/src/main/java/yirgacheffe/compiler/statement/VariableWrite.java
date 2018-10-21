package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
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
		String name,
		Expression expression,
		Coordinate coordinate)
	{
		this.name = name;
		this.expression = expression;
		this.coordinate = coordinate;
	}

	@Override
	public boolean returns()
	{
		return false;
	}

	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		Type type = this.expression.getType(variables);
		Variable variable = variables.getVariable(this.name);
		Type variableType = variable.getType();
		Array<Error> errors = new Array<>();

		this.expression.compile(methodVisitor, variables);

		if (!type.isAssignableTo(variableType))
		{
			String message =
				"Cannot assign expression of type " +
				type + " to variable of type " +
				variableType + ".";

			errors.push(new Error(this.coordinate, message));
		}

		methodVisitor.visitVarInsn(type.getStoreInstruction(), variable.getIndex());

		variables.write(this);

		return new StatementResult(errors);
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}

	public String getName()
	{
		return this.name;
	}
}
