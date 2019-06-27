package yirgacheffe.compiler.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
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

	public Result compile(Variables variables, Signature caller)
	{
		Result result = new Result();
		Type type = this.condition.getType(variables);

		Label trueLabel = new Label();

		if (this.condition.isCondition(variables))
		{
			result = result.concat(
				this.condition.compileCondition(
					variables,
					trueLabel,
					this.falseLabel));
		}
		else if (type.equals(PrimitiveType.DOUBLE))
		{
			result = result
				.concat(this.condition.compile(variables))
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Falsyfier",
					"isTruthy",
					"(D)Z",
					false))
				.add(new JumpInsnNode(
					Opcodes.IFEQ,
					new LabelNode(this.falseLabel)));
		}
		else if (type.isPrimitive())
		{
			result = result
				.concat(this.condition.compile(variables))
				.add(new JumpInsnNode(
					Opcodes.IFEQ,
					new LabelNode(this.falseLabel)));
		}
		else if (type.isAssignableTo(new ReferenceType(String.class)))
		{
			result = result
				.concat(this.condition.compile(variables))
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Falsyfier",
					"isTruthy",
					"(Ljava/lang/String;)Z",
					false))
				.add(new JumpInsnNode(
					Opcodes.IFEQ,
					new LabelNode(this.falseLabel)));
		}
		else
		{
			result = result
				.concat(this.condition.compile(variables))
				.add(new JumpInsnNode(
					Opcodes.IFNULL,
					new LabelNode(this.falseLabel)));
		}

		return result
			.add(new LabelNode(trueLabel))
			.concat(this.statement.compile(variables, caller));
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
