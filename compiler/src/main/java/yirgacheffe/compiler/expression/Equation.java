package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.comparison.Comparator;
import yirgacheffe.compiler.comparison.Equals;
import yirgacheffe.compiler.comparison.NotEquals;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Equation implements Expression
{
	private Coordinate coordinate;

	private Comparator comparator;

	private Expression firstOperand;

	private Expression secondOperand;

	public Equation(
		Coordinate coordinate,
		Comparator comparator,
		Expression firstOperand,
		Expression secondOperand)
	{
		this.coordinate = coordinate;
		this.comparator = comparator;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.BOOLEAN;
	}

	public Result compile(Variables variables)
	{
		Type string = new ReferenceType(String.class);

		if (this.firstOperand.getType(variables).isAssignableTo(string) &&
			this.secondOperand.getType(variables).isAssignableTo(string) &&
			(this.comparator instanceof Equals))
		{
			return this.compareStrings(
				variables,
				this.firstOperand,
				this.secondOperand);
		}

		Label label	= new Label();
		Label falseLabel = new Label();

		return this.compileCondition(variables, null, label)
			.add(new InsnNode(Opcodes.ICONST_1))
			.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(falseLabel)))
			.add(new LabelNode(label))
			.add(new InsnNode(Opcodes.ICONST_0))
			.add(new LabelNode(falseLabel));
	}

	private Result compareStrings(
		Variables variables,
		Expression firstOperand,
		Expression secondOperand)
	{
		Result result = new Result()
			.concat(firstOperand.compile(variables))
			.concat(secondOperand.compile(variables))
			.add(new MethodInsnNode(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/String",
				"equals",
				"(Ljava/lang/Object;)Z",
				false));

		variables.stackPop();
		variables.stackPop();
		variables.stackPush(this.getType(variables));

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);
		Type type = firstType.intersect(secondType);
		Result result = new Result();

		if (firstType.isPrimitive() ^ secondType.isPrimitive() ||
			!type.isAssignableTo(PrimitiveType.DOUBLE) &&
				!(this.comparator instanceof Equals ||
					this.comparator instanceof NotEquals))
		{
			String message =
				"Cannot compare " + firstType + " and " + secondType + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		result = result
			.concat(this.firstOperand.compile(variables))
			.concat(firstType.convertTo(type))
			.concat(this.secondOperand.compile(variables))
			.concat(secondType.convertTo(type))
			.concat(this.comparator.compile(falseLabel, type));

		variables.stackPop();
		variables.stackPop();
		variables.stackPush(this.getType(variables));

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
