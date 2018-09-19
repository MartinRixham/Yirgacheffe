package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;

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

	@Override
	public void compile(MethodVisitor methodVisitor, StatementResult result)
	{
		this.precondition.compile(methodVisitor, result);

		methodVisitor.visitJumpInsn(Opcodes.GOTO, this.label);

		if (this.precondition instanceof If)
		{
			If ifStatement = (If) this.precondition;

			methodVisitor.visitLabel(ifStatement.getLabel());
		}
		else
		{
			String message = "Else not preceded by if statement.";

			result.error(new Error(this.coordinate, message));
		}

		this.statement.compile(methodVisitor, result);
	}

	@Override
	public Label getLabel()
	{
		return this.label;
	}
}
