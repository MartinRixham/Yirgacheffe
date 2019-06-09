package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Error;
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

	private Label falseLabel = new Label();

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

		Label trueLabel = new Label();

		if (this.condition.isCondition(variables))
		{
			errors =
				errors.concat(
					this.condition.compileCondition(
						methodVisitor,
						variables,
						trueLabel,
						this.falseLabel));
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

			methodVisitor.visitJumpInsn(Opcodes.IFEQ, this.falseLabel);
		}
		else if (type.isPrimitive())
		{
			errors = errors.concat(this.condition.compile(methodVisitor, variables));

			methodVisitor.visitJumpInsn(Opcodes.IFEQ, this.falseLabel);
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

			methodVisitor.visitJumpInsn(Opcodes.IFEQ, this.falseLabel);
		}
		else
		{
			errors = errors.concat(this.condition.compile(methodVisitor, variables));

			methodVisitor.visitJumpInsn(Opcodes.IFNULL, this.falseLabel);
		}

		methodVisitor.visitLabel(trueLabel);

		errors.push(this.statement.compile(methodVisitor, variables, caller));

		return errors;
	}

	public Label getLabel()
	{
		return this.falseLabel;
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
