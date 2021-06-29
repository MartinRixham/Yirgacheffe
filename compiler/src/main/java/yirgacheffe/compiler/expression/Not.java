package yirgacheffe.compiler.expression;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Not implements Expression
{
	private Coordinate coordinate;

	private Expression expression;

	private boolean flip;

	public Not(Coordinate coordinate, Expression expression, long multiplicity)
	{
		this.coordinate = coordinate;
		this.expression = expression;
		this.flip = multiplicity % 2 != 0;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.BOOLEAN;
	}

	public Result compile(Variables variables)
	{
		Label trueLabel = new Label();
		Label falseLabel = new Label();
		Label doneLabel = new Label();
		boolean flip = this.flip;

		return this.compileCondition(variables, trueLabel, falseLabel)
			.add(new LabelNode(flip ? falseLabel : trueLabel))
			.add(new InsnNode(flip ? Opcodes.ICONST_0 : Opcodes.ICONST_1))
			.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(doneLabel)))
			.add(new LabelNode(flip ? trueLabel : falseLabel))
			.add(new InsnNode(flip ? Opcodes.ICONST_1 : Opcodes.ICONST_0))
			.add(new LabelNode(doneLabel));
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		Type type = this.expression.getType(variables);
		boolean flip = this.flip;

		if (this.expression.isCondition(variables))
		{

			return new Result()
				.concat(this.expression.compileCondition(
					variables,
					flip ? falseLabel : trueLabel,
					flip ? trueLabel : falseLabel));
		}
		else
		{
			BooleanOperator booleanOperator =
				flip ? BooleanOperator.OR : BooleanOperator.AND;

			return new Result()
				.concat(this.expression.compile(variables))
				.concat(type.attempt())
				.concat(type.compare(booleanOperator, falseLabel));
		}
	}

	public boolean isCondition(Variables variables)
	{
		return true;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}
}
