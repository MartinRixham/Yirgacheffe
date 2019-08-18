package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.comparison.BooleanComparison;
import yirgacheffe.compiler.comparison.Comparator;
import yirgacheffe.compiler.comparison.Comparison;
import yirgacheffe.compiler.comparison.Equals;
import yirgacheffe.compiler.comparison.NotEquals;
import yirgacheffe.compiler.comparison.NumberComparison;
import yirgacheffe.compiler.comparison.ObjectComparison;
import yirgacheffe.compiler.comparison.StringComparison;
import yirgacheffe.compiler.error.Coordinate;
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

		return
			this.firstOperand.compile(variables)
			.concat(this.continueCompilation(variables, null, label))
			.add(new InsnNode(Opcodes.ICONST_1))
			.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(falseLabel)))
			.add(new LabelNode(label))
			.add(new InsnNode(Opcodes.ICONST_0))
			.add(new LabelNode(falseLabel));
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.firstOperand.compile(variables)
			.concat(this.continueCompilation(variables, trueLabel, falseLabel));
	}

	private Result continueCompilation(
		Variables variables,
		Label trueLabel,
		Label falseLabel)
	{
		Type string = new ReferenceType(String.class);
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);

		Comparison comparison;

		if (firstType.isAssignableTo(string) && secondType.isAssignableTo(string) &&
			(this.comparator instanceof Equals || this.comparator instanceof NotEquals))
		{
			comparison =
				new StringComparison(
					this.comparator,
					this.firstOperand,
					this.secondOperand);
		}
		else if (firstType.equals(PrimitiveType.BOOLEAN))
		{
			comparison =
				new BooleanComparison(
					this.coordinate,
					this.comparator,
					this.firstOperand,
					this.secondOperand);
		}
		else if (firstType.isAssignableTo(PrimitiveType.DOUBLE))
		{
			comparison =
				new NumberComparison(
					this.coordinate,
					this.comparator,
					this.firstOperand,
					this.secondOperand);
		}
		else
		{
			comparison =
				new ObjectComparison(
					this.coordinate,
					this.comparator,
					this.firstOperand,
					this.secondOperand);
		}

		Result result = new Result();

		if (this.secondOperand instanceof Equation)
		{
			Equation secondEquation = (Equation) this.secondOperand;
			Expression secondOperand = secondEquation.getFirstOperand();
			Label trulabel = new Label();
			Label falsLabel = new Label();

			result = result
				.concat(secondOperand.compile(variables))
				.add(this.getDupcode(firstType, secondOperand.getType(variables)))
				.concat(this.comparator.compile(falsLabel, firstType))
				.concat(secondEquation.continueCompilation(
					variables,
					trueLabel,
					falseLabel))
				.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(trulabel)))
				.add(new LabelNode(falsLabel))
				.add(new InsnNode(Opcodes.POP2))
				.add(new JumpInsnNode(Opcodes.GOTO, new LabelNode(falseLabel)))
				.add(new LabelNode(trulabel));
		}
		else
		{
			result = result
				.concat(comparison.compile(variables, falseLabel));

			variables.stackPop();
			variables.stackPop();
		}

		variables.stackPush(this.getType(variables));

		return result;
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

	private Expression getFirstOperand()
	{
		return this.firstOperand;
	}

	private InsnNode getDupcode(Type firstType, Type secondType)
	{
		/*int opcode;

		if (firstType.width() == 2)
		{
			if (secondType.width() == 2)
			{
				opcode = Opcodes.DUP2_X2;
			}
			else
			{
				opcode = Opcodes.DUP_X2;
			}
		}
		else
		{
			if (secondType.width() == 2)
			{
				opcode = Opcodes.DUP2_X1;
			}
			else
			{
				opcode = Opcodes.DUP_X1;
			}
		}*/

		return new InsnNode(Opcodes.DUP2_X2);
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
