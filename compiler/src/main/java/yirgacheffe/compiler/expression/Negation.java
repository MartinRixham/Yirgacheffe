package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Negation implements Expression
{
	private Coordinate coordinate;

	private Expression expression;

	public Negation(Coordinate coordinate, Expression expression)
	{
		this.coordinate = coordinate;
		this.expression = expression;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.DOUBLE;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = this.expression.compile(methodVisitor, variables);
		Type type = this.expression.getType(variables);

		if (!type.isAssignableTo(PrimitiveType.DOUBLE))
		{
			String message = "Cannot negate " + type + ".";

			errors.push(new Error(this.coordinate, message));
		}

		methodVisitor.visitInsn(Opcodes.DNEG);

		return errors;
	}

	public Array<Error> compileCondition(
		MethodVisitor methodVisitor,
		Variables variables,
		Label label)
	{
		return this.compile(methodVisitor, variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}
}
