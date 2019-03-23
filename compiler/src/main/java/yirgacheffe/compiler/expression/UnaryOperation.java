package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.statement.Statement;
import yirgacheffe.compiler.statement.VariableWrite;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variable;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class UnaryOperation implements Expression, Statement
{
	private Coordinate coordinate;

	private Expression expression;

	private boolean pre;

	private boolean increment;

	public UnaryOperation(
		Coordinate coordinate,
		Expression expression,
		boolean pre,
		boolean increment)
	{
		this.coordinate = coordinate;
		this.expression = expression;
		this.pre = pre;
		this.increment = increment;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.DOUBLE;
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = this.expression.compile(methodVisitor, variables);

		if (!this.pre)
		{
			methodVisitor.visitInsn(Opcodes.DUP2);
		}

		methodVisitor.visitInsn(Opcodes.DCONST_1);
		methodVisitor.visitInsn(this.increment ? Opcodes.DADD : Opcodes.DNEG);

		if (this.pre)
		{
			methodVisitor.visitInsn(Opcodes.DUP2);
		}

		this.checkType(variables, errors);
		this.updateVariable(variables, methodVisitor);

		return errors;
	}

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		Array<Error> errors = this.expression.compile(methodVisitor, variables);

		methodVisitor.visitInsn(Opcodes.DCONST_1);
		methodVisitor.visitInsn(this.increment ? Opcodes.DADD : Opcodes.DNEG);

		this.checkType(variables, errors);
		this.updateVariable(variables, methodVisitor);

		return errors;
	}

	private void checkType(Variables variables, Array<Error> errors)
	{
		Type type = this.expression.getType(variables);

		if (type != PrimitiveType.DOUBLE)
		{
			String increment = this.increment ? "increment" : "decrement";
			String message = "Cannot " + increment + " " + type + ".";

			errors.push(new Error(this.coordinate, message));
		}
	}

	private void updateVariable(Variables variables, MethodVisitor methodVisitor)
	{
		if (this.expression instanceof VariableRead)
		{
			VariableRead read = (VariableRead) this.expression;
			Variable variable = variables.getVariable(read.getName());

			methodVisitor.visitVarInsn(Opcodes.DSTORE, variable.getIndex());
		}
	}

	public boolean returns()
	{
		return false;
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
}
