package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
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

		result = result.concat(firstType.convertTo(type));

		if (!type.equals(firstType))
		{
			result = result.concat(firstType.swapWith(type));
		}

		result = result.concat(this.compileComparison(this.operator, label, firstType));

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
			.concat(this.compileComparison(
				this.operator,
				label,
				firstType));

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
				.concat(this.compileComparison(
					BooleanOperator.AND,
					falseLabel,
					secondType));
		}

		return result;
	}

	private Result compileComparison(BooleanOperator operator, Label label, Type type)
	{
		Result result = new Result();

		if (type.equals(PrimitiveType.DOUBLE))
		{
			result = result
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Falsyfier",
					"isTruthy",
					"(D)Z",
					false))
				.add(new JumpInsnNode(
					operator.integerOpcode(),
					new LabelNode(label)));
		}
		else if (type.isPrimitive())
		{
			result = result
				.add(new JumpInsnNode(
					operator.integerOpcode(),
					new LabelNode(label)));
		}
		else if (type.isAssignableTo(new ReferenceType(String.class)))
		{
			result = result
				.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Falsyfier",
					"isTruthy",
					"(Ljava/lang/String;)Z",
					false))
				.add(new JumpInsnNode(
					operator.integerOpcode(),
					new LabelNode(label)));
		}
		else
		{
			result = result.add(
				new JumpInsnNode(
					operator.referenceOpcode(),
					new LabelNode(label)));
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
}
