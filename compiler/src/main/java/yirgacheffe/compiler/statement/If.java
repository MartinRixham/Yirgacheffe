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
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
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
		Type type = this.condition.getType(variables);

		if (this.condition instanceof Equation)
		{
			Equation equation = (Equation) this.condition;

			equation.compileComparison(methodVisitor, variables, this.label);
		}
		else if (type.equals(PrimitiveType.DOUBLE))
		{
			errors = errors.concat(this.condition.compile(methodVisitor, variables));

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"yirgacheffe/lang/Falsyfier",
				"isTruthy",
				"(D)Z",
				false);

			methodVisitor.visitJumpInsn(Opcodes.IFEQ, this.label);
		}
		else if (type.isPrimitive())
		{
			errors = errors.concat(this.condition.compile(methodVisitor, variables));

			methodVisitor.visitJumpInsn(Opcodes.IFEQ, this.label);
		}
		else if (type.isAssignableTo(new ReferenceType(String.class)))
		{
			errors = errors.concat(this.condition.compile(methodVisitor, variables));

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				"yirgacheffe/lang/Falsyfier",
				"isTruthy",
				"(Ljava/lang/String;)Z",
				false);

			methodVisitor.visitJumpInsn(Opcodes.IFEQ, this.label);
		}
		else
		{
			errors = errors.concat(this.condition.compile(methodVisitor, variables));

			methodVisitor.visitJumpInsn(Opcodes.IFNULL, this.label);
		}

		errors.push(this.statement.compile(methodVisitor, variables, caller));

		return errors;
	}

	public Label getLabel()
	{
		return this.label;
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
