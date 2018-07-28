package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ReturnTypeError;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.Type;

public class Return implements Statement
{
	private Coordinate coordinate;

	private Type type;

	private Expression expression;

	public Return(Coordinate coordinate, Type type, Expression expression)
	{
		this.coordinate = coordinate;
		this.type = type;
		this.expression = expression;
	}

	@Override
	public void compile(MethodVisitor methodVisitor, StatementResult result)
	{
		Type type = this.expression.check(result);

		this.expression.compile(methodVisitor);

		if (!type.isAssignableTo(this.type))
		{
			ReturnTypeError message =
				new ReturnTypeError(this.type, type);

			result.error(new Error(this.coordinate, message));
		}
	}
}
