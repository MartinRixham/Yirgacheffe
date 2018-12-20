package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

public class Else implements ConditionalStatement
{
	private Coordinate coordinate;

	private Statement precondition;

	private Statement statement;

	private Label label = new Label();

	public Else(Coordinate coordinate, Statement precondition, Statement statement)
	{
		this.coordinate = coordinate;
		this.precondition = precondition;
		this.statement = statement;
	}

	public boolean returns()
	{
		return this.precondition.returns() && this.statement.returns();
	}

	public Array<Error> compile(MethodVisitor methodVisitor, Variables variables)
	{
		Array<Error> errors = this.precondition.compile(methodVisitor, variables);

		methodVisitor.visitJumpInsn(Opcodes.GOTO, this.label);

		if (this.precondition instanceof If)
		{
			If ifStatement = (If) this.precondition;

			methodVisitor.visitLabel(ifStatement.getLabel());
		}
		else
		{
			String message = "Else not preceded by if statement.";

			errors.push(new Error(this.coordinate, message));
		}

		errors.push(this.statement.compile(methodVisitor, variables));

		return errors;
	}

	public Label getLabel()
	{
		return this.label;
	}

	public Expression getFirstOperand()
	{
		return this.precondition.getFirstOperand();
	}

	@Override
	public Array<VariableRead> getVariableReads()
	{
		return this.statement.getVariableReads()
			.concat(this.precondition.getVariableReads());
	}

	@Override
	public Array<VariableWrite> getVariableWrites()
	{
		return this.statement.getVariableWrites()
			.concat(this.precondition.getVariableWrites());
	}
}
