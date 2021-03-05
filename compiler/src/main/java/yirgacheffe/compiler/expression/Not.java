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

	private BooleanOperator booleanOperator;

	public Not(Coordinate coordinate, Expression expression, long multiplicity)
	{
		this.coordinate = coordinate;
		this.expression = expression;

		this.booleanOperator =
			multiplicity % 2 == 0 ? BooleanOperator.AND : BooleanOperator.OR;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.BOOLEAN;
	}

	public Result compile(Variables variables)
	{
		Label trueLabel = new Label();
		Label falseLabel = new Label();

		return this.compileCondition(variables, trueLabel, falseLabel)
			.add(new InsnNode(Opcodes.ICONST_1))
			.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(trueLabel)))
			.add(new LabelNode(falseLabel))
			.add(new InsnNode(Opcodes.ICONST_0))
			.add(new LabelNode(trueLabel));
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		Type type = this.expression.getType(variables);
		Result result;

		if (this.expression.isCondition(variables))
		{
			result = new Result()
				.concat(
					this.expression.compileCondition(variables, trueLabel, falseLabel));
		}
		else
		{
			result = new Result()
				.concat(this.expression.compile(variables));
		}

		return result
			.concat(type.compare(this.booleanOperator, falseLabel));
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
