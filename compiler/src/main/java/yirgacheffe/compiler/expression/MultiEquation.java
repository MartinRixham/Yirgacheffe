package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.comparison.Comparator;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class MultiEquation implements Expression
{
	private Coordinate coordinate;

	private Array<Comparator> comparators;

	private Array<Expression> expressions;

	public MultiEquation(
		Coordinate coordinate,
		Array<Comparator> comparators,
		Array<Expression> expressions)
	{
		this.coordinate = coordinate;
		this.comparators = comparators;
		this.expressions = expressions;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.BOOLEAN;
	}

	public Result compile(Variables variables)
	{
		Label falseLabel = new Label();
		Label trueLabel = new Label();
		Label truLabel = new Label();

		return this.compileCondition(variables, trueLabel, falseLabel)
			.add(new LabelNode(trueLabel))
			.add(new InsnNode(Opcodes.ICONST_1))
			.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(truLabel)))
			.add(new LabelNode(falseLabel))
			.add(new InsnNode(Opcodes.ICONST_0))
			.add(new LabelNode(truLabel));
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		Type type = this.getIntersectionType(variables);
		Label falsLabel = new Label();
		Expression firstExpression = this.expressions.get(0);

		Result result = new Result()
			.concat(firstExpression.compile(variables))
			.concat(firstExpression.getType(variables).convertTo(type));

		for (int i = 1; i < this.expressions.length() - 1; i++)
		{
			Comparator comparator = this.comparators.get(i - 1);
			Expression expression = this.expressions.get(i);

			result = result
				.concat(expression.compile(variables))
				.concat(expression.getType(variables).convertTo(type))
				.add(this.getDupcode(type))
				.concat(comparator.compile(falsLabel, type));

			variables.stackPop();
		}

		Expression lastOperand = this.expressions.get(this.expressions.length() - 1);
		Comparator lastComparator = this.comparators.get(this.comparators.length() - 1);

		result = result
			.concat(lastOperand.compile(variables))
			.concat(lastOperand.getType(variables).convertTo(type))
			.concat(lastComparator.compile(falseLabel, type))
			.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(trueLabel)))
			.add(new LabelNode(falsLabel))
			.add(this.getPopcode(type))
			.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(falseLabel)));

		variables.stackPop();
		variables.stackPop();
		variables.stackPush(this.getType(variables));

		return result;
	}

	private Type getIntersectionType(Variables variables)
	{
		Type intersection = this.expressions.get(0).getType(variables);

		for (int i = 1; i < this.expressions.length(); i++)
		{
			intersection =
				intersection.intersect(this.expressions.get(i).getType(variables));
		}

		return intersection;
	}

	private InsnNode getDupcode(Type type)
	{
		if (type.width() == 1)
		{
			return new InsnNode(Opcodes.DUP_X1);
		}
		else
		{
			return new InsnNode(Opcodes.DUP2_X2);
		}
	}

	private InsnNode getPopcode(Type type)
	{
		if (type.width() == 1)
		{
			return new InsnNode(Opcodes.POP);
		}
		else
		{
			return new InsnNode(Opcodes.POP2);
		}
	}

	public boolean isCondition(Variables variables)
	{
		return true;
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}
}
