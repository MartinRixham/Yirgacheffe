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

public class If implements ConditionalStatement
{
	private Expression condition;

	private Statement statement;

	private Label label = new Label();

	public If(Expression condition, Statement statement)
	{
		this.condition = condition;
		this.statement = statement;
	}

	public boolean returns()
	{
		return this.statement.returns();
	}

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		Array<Error> errors = new Array<>();

		if (this.condition instanceof Equation)
		{
			Equation equation = (Equation) this.condition;

			equation.compileCondition(methodVisitor, variables, this.label);
		}
		else
		{
			errors.push(this.condition.compile(methodVisitor, variables));

			methodVisitor.visitJumpInsn(Opcodes.IFEQ, this.label);
		}

		errors.push(this.statement.compile(methodVisitor, variables, caller));

		return errors;
	}

	public Label getLabel()
	{
		return this.label;
	}

	public Expression getFirstOperand()
	{
		return this.condition.getFirstOperand();
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.condition.getVariableReads()
			.concat(this.statement.getVariableReads());
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return this.statement.getVariableWrites();
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
