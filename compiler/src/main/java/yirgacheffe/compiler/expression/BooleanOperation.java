package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.compiler.type.IntersectionType;
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

		return new IntersectionType(firstType, secondType);
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

		if (firstType.isPrimitive())
		{
			if (!type.isPrimitive())
			{
				result = result.concat(this.compileBoxingCall(firstType));

				if (firstType.width() == 2)
				{
					result = result
						.add(new InsnNode(Opcodes.DUP_X2))
						.add(new InsnNode(Opcodes.POP));
				}
				else
				{
					result = result.add(new InsnNode(Opcodes.SWAP));
				}
			}
			else if (((PrimitiveType) firstType).order() <
				((PrimitiveType) secondType).order())
			{
				PrimitiveType firstPrimitive = (PrimitiveType) firstType;
				PrimitiveType secondPrimitive = (PrimitiveType) secondType;

				result = result.add(
					new InsnNode(firstPrimitive.convertTo(secondPrimitive)));

				if (secondPrimitive.width() == 2)
				{
					result = result
						.add(new InsnNode(Opcodes.DUP2_X1))
						.add(new InsnNode(Opcodes.POP2));
				}
				else
				{
					result = result.add(new InsnNode(Opcodes.SWAP));
				}
			}
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

		result = result.concat(this.secondOperand.compile(variables));

		if (secondType.isPrimitive())
		{
			if (!type.isPrimitive())
			{
				result = result.concat(this.compileBoxingCall(secondType));
			}
			else if (((PrimitiveType) secondType).order() <
				((PrimitiveType) firstType).order())
			{
				PrimitiveType firstPrimitive = (PrimitiveType) firstType;
				PrimitiveType secondPrimitive = (PrimitiveType) secondType;

				result = result.add(
					new InsnNode(secondPrimitive.convertTo(firstPrimitive)));
			}
		}

		result = result.add(new LabelNode(label));

		return result;
	}

	private Result compileBoxingCall(Type type)
	{
		Result result = new Result();

		String descriptor =
			"(" + type.toJVMType() + ")L" +
				type.toFullyQualifiedType() + ";";

		return result.add(new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			type.toFullyQualifiedType(),
			"valueOf",
			descriptor,
			false));
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
