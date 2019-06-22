package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

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

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		Type type = this.expression.getType(variables);
		Array<Error> errors = new Array<>();

		if (type.isAssignableTo(this.type))
		{
			errors = errors.concat(this.expression.compile(methodVisitor, variables));

			methodVisitor.visitInsn(this.type.getReturnInstruction());
		}
		else if (type.isAssignableTo(new ReferenceType(Throwable.class)))
		{
			errors = errors.concat(this.expression.compile(methodVisitor, variables));

			methodVisitor.visitInsn(Opcodes.ATHROW);
		}
		else
		{
			methodVisitor.visitInsn(this.type.getZero());
			methodVisitor.visitInsn(this.type.getReturnInstruction());

			String message =
				"Mismatched return type: Cannot return expression of type " +
				type + " from method of return type " +
				this.type + ".";

			errors.push(new Error(this.coordinate, message));
		}

		return errors;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
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
