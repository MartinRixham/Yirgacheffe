package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
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

	public StatementResult compile(MethodVisitor methodVisitor, Variables variables)
	{
		StatementResult ifResult = this.precondition.compile(methodVisitor, variables);
		Array<Error> errors = new Array<>();

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

		StatementResult blockResult = this.statement.compile(methodVisitor, variables);
		boolean returns = ifResult.returns() && blockResult.returns();

		errors = errors.concat(ifResult.getErrors()).concat(blockResult.getErrors());

		return new StatementResult(returns, errors);
	}

	public Label getLabel()
	{
		return this.label;
	}
}
