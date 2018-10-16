package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.ExpressionResult;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Return implements Statement
{
	private Coordinate coordinate;

	private Type type = PrimitiveType.VOID;

	private Expression expression;

	public Return(Coordinate coordinate, Type type, Expression expression)
	{
		this.coordinate = coordinate;
		this.type = type;
		this.expression = expression;
	}

	public Return(Coordinate coordinate)
	{
		this.coordinate = coordinate;
	}

	@Override
	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		Type type = PrimitiveType.VOID;
		Array<Error> errors = new Array<>();
		ExpressionResult result = new ExpressionResult();

		if (this.expression != null)
		{
			type = this.expression.check(variables);
			result = result.add(this.expression.compile(methodVisitor));

			methodVisitor.visitInsn(type.getReturnInstruction());
		}
		else
		{
			methodVisitor.visitInsn(Opcodes.RETURN);
		}

		if (!type.isAssignableTo(this.type))
		{
			String message =
				"Mismatched return type: Cannot return expression of type " +
				type + " from method of return type " +
				this.type + ".";

			errors.push(new Error(this.coordinate, message));
		}

		return new StatementResult(true, result.getErrors().concat(errors));
	}
}
