package yirgacheffe.compiler.expression;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.operator.Operator;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class BinaryOperation implements Expression
{
	private Coordinate coordinate;

	private Operator operator;

	private Expression firstOperand;

	private Expression secondOperand;

	public BinaryOperation(
		Coordinate coordinate,
		Operator operator,
		Expression firstOperand,
		Expression secondOperand)
	{
		this.coordinate = coordinate;
		this.operator = operator;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
	}

	public Type getType(Variables variables)
	{
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);
		Type string = new ReferenceType(String.class);

		if (firstType.isAssignableTo(string) || secondType.isAssignableTo(string))
		{
			return string;
		}
		else if (this.operator == Operator.DIVIDE)
		{
			return PrimitiveType.DOUBLE;
		}
		else
		{
			return firstType.intersect(secondType);
		}
	}

	public Result compile(Variables variables)
	{
		Type firstType = this.firstOperand.getType(variables);
		Type secondType = this.secondOperand.getType(variables);
		Type string = new ReferenceType(String.class);

		if (this.operator == Operator.ADD  &&
			(firstType.isAssignableTo(string) || secondType.isAssignableTo(string)))
		{
			return this.compileStrings(variables);
		}

		Result result = new Result();

		if (!firstType.isAssignableTo(PrimitiveType.DOUBLE) ||
			!secondType.isAssignableTo(PrimitiveType.DOUBLE))
		{
			String message =
				"Cannot " + this.operator.getDescription() + " " +
				firstType + " and " + secondType + ".";

			return result
				.add(new Error(this.coordinate, message))
				.concat(this.firstOperand.compile(variables));
		}

		Type type = this.getType(variables);

		result = result
			.concat(this.firstOperand.compile(variables))
			.concat(firstType.convertTo(type))
			.concat(this.secondOperand.compile(variables))
			.concat(secondType.convertTo(type));

		if (type.equals(PrimitiveType.INT))
		{
			result = result.add(new InsnNode(this.operator.getIntOpcode()));
		}
		else if (type.equals(PrimitiveType.LONG))
		{
			result = result.add(new InsnNode(this.operator.getLongOpcode()));
		}
		else
		{
			result = result.add(new InsnNode(this.operator.getDoubleOpcode()));
		}

		variables.stackPop();
		variables.stackPop();
		variables.stackPush(type);

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return this.compile(variables);
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	private Result compileStrings(Variables variables)
	{
		Type firstOperandType = this.firstOperand.getType(variables);

		Result result = new Result()
			.concat(this.firstOperand.compile(variables))
			.add(new MethodInsnNode(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/StringBuilder",
				"append",
				"(" + firstOperandType.toJVMType() + ")Ljava/lang/StringBuilder;",
				false))
			.concat(this.secondOperand.compile(variables));

		variables.stackPop();
		variables.stackPop();
		variables.stackPush(this.getType(variables));

		return result;
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.firstOperand.getVariableReads()
			.concat(this.secondOperand.getVariableReads());
	}

	public String getSecondOperandType(Variables variables)
	{
		return this.secondOperand.getType(variables).toJVMType();
	}
}
