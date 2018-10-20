package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Equals implements Expression
{
	private Expression firstOperand;

	private Expression secondOperand;

	public Equals(Expression firstOperand, Expression secondOperand)
	{
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.BOOLEAN;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = new Array<>();

		errors.concat(this.firstOperand.compile(methodVisitor, variables));
		errors.concat(this.secondOperand.compile(methodVisitor, variables));

		return errors;
	}
}
