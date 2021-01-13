package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class BooleanOperation implements Expression
{
	private BooleanOperator operator;

	private Expression firstOperand;

	private Expression secondOperand;

	public BooleanOperation(
		BooleanOperator operator,
		Expression firstOperand,
		Expression secondOperand)
	{
		this.operator = operator;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Type getType(Variables variables)
	{
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		return firstType.intersect(secondType);
	}

	public Result compile(Variables variables)
	{
		Result result = new Result();
		Label label = new Label();
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);
		Type type = this.getType(variables);

		result = result.concat(this.firstOperand.compile(variables));

		if (firstType.width() == 2)
		{
			result = result.add(new InsnNode(Opcodes.DUP2));
		}
		else
		{
			result = result.add(new InsnNode(Opcodes.DUP));
		}

		if (!type.equals(firstType))
		{
			result = result
				.concat(firstType.convertTo(type))
				.concat(firstType.swapWith(type));
		}

		result = result.concat(firstType.compare(this.operator, label));

		if (type.width() == 2)
		{
			result = result.add(new InsnNode(Opcodes.POP2));
		}
		else
		{
			result = result.add(new InsnNode(Opcodes.POP));
		}

		result = result
			.concat(this.secondOperand.compile(variables))
			.concat(secondType.convertTo(type))
			.add(new LabelNode(label));

		variables.stackPop();
		variables.stackPop();
		variables.stackPush(type);

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		Result result = new Result();
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);
		Label label = this.operator == BooleanOperator.OR ? trueLabel : falseLabel;

		result = result
			.concat(this.firstOperand.compile(variables))
			.concat(firstType.compare(
				this.operator,
				label));

		variables.stackPop();

		if (this.secondOperand.isCondition(variables))
		{
			result = result.concat(
				this.secondOperand.compileCondition(
					variables,
					trueLabel,
					falseLabel));
		}
		else
		{
			result = result
				.concat(this.secondOperand.compile(variables))
				.concat(secondType.compare(
					BooleanOperator.AND,
					falseLabel));
		}

		return result;
	}

	public boolean isCondition(Variables variables)
	{
		return true;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}

	public Coordinate getCoordinate()
	{
		return this.firstOperand.getCoordinate();
	}
}
