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

	public UnaryOperation(Coordinate coordinate, Expression expression, boolean pre)
	{
		this.coordinate = coordinate;
		this.expression = expression;
		this.pre = pre;
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
		methodVisitor.visitInsn(Opcodes.DADD);

		if (this.pre)
		{
			methodVisitor.visitInsn(Opcodes.DUP2);
		}

		Type type = this.expression.getType(variables);

		if (type != PrimitiveType.DOUBLE)
		{
			String message = "Cannot increment " + type + ".";

			errors.push(new Error(this.coordinate, message));
		}

		if (this.expression instanceof VariableRead)
		{
			VariableRead read = (VariableRead) this.expression;
			Variable variable = variables.getVariable(read.getName());

			methodVisitor.visitVarInsn(Opcodes.DSTORE, variable.getIndex());
		}

		return errors;
	}

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		Array<Error> errors = this.expression.compile(methodVisitor, variables);

		methodVisitor.visitInsn(Opcodes.DCONST_1);
		methodVisitor.visitInsn(Opcodes.DADD);

		Type type = this.expression.getType(variables);

		if (type != PrimitiveType.DOUBLE)
		{
			String message = "Cannot increment " + type + ".";

			errors.push(new Error(this.coordinate, message));
		}

		if (this.expression instanceof VariableRead)
		{
			VariableRead read = (VariableRead) this.expression;
			Variable variable = variables.getVariable(read.getName());

			methodVisitor.visitVarInsn(Opcodes.DSTORE, variable.getIndex());
		}

		return errors;
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
