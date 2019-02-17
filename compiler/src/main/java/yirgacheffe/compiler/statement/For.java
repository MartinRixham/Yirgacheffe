package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Equation;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class For implements Statement
{
	private Statement initialiser;

	private Expression exitCondition;

	private Statement incrementer;

	private Array<Statement> statements;

	public For(
		Statement initialiser,
		Expression exitCondition,
		Statement incrementer,
		Array<Statement> statements)
	{
		this.initialiser = initialiser;
		this.exitCondition = exitCondition;
		this.incrementer = incrementer;
		this.statements = statements;
	}

	@Override
	public boolean returns()
	{
		return false;
	}

	@Override
	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		Array<Error> errors = this.initialiser.compile(methodVisitor, variables, caller);

		Label exitLabel = new Label();
		Label continueLabel = new Label();

		methodVisitor.visitLabel(continueLabel);

		if (this.exitCondition instanceof Equation)
		{
			Equation equation = (Equation) this.exitCondition;

			equation.compileCondition(methodVisitor, variables, exitLabel);
		}
		else
		{
			errors = errors.concat(this.exitCondition.compile(methodVisitor, variables));

			methodVisitor.visitJumpInsn(Opcodes.IFEQ, exitLabel);
		}

		for (Statement statement : this.statements)
		{
			errors = errors.concat(statement.compile(methodVisitor, variables, caller));
		}

		errors = errors.concat(
			this.incrementer.compile(methodVisitor, variables, caller));

		methodVisitor.visitJumpInsn(Opcodes.GOTO, continueLabel);

		methodVisitor.visitLabel(exitLabel);

		return errors;
	}

	@Override
	public Array<VariableRead> getVariableReads()
	{
		return this.initialiser.getVariableReads();
	}

	@Override
	public Array<VariableWrite> getVariableWrites()
	{
		return this.initialiser.getVariableWrites();
	}

	@Override
	public Expression getExpression()
	{
		if (this.statements.length() == 0)
		{
			return new Nothing();
		}
		else
		{
			return this.statements.get(this.statements.length() - 1).getExpression();
		}
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}
}
