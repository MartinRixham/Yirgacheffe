package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ErrorMessage;
import yirgacheffe.compiler.error.VariableAssignmentError;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;

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
	public boolean compile(MethodVisitor methodVisitor, StatementResult result)
	{
		Type type = this.expression.check(result);
		Variable variable = result.getVariable(this.name);
		Type variableType = variable.getType();

		this.expression.compile(methodVisitor);

		if (!type.isAssignableTo(variableType))
		{
			ErrorMessage message = new VariableAssignmentError(variableType, type);

			result.error(new Error(this.coordinate, message));
		}

		methodVisitor.visitVarInsn(type.getStoreInstruction(), variable.getIndex());

		result.write(this);

		return false;
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
