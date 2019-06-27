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
import yirgacheffe.compiler.type.Variables;
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
		else if (firstType.equals(PrimitiveType.DOUBLE) ||
			secondType.equals(PrimitiveType.DOUBLE))
		{
			return PrimitiveType.DOUBLE;
		}
		else if (firstType.equals(PrimitiveType.LONG) ||
			secondType.equals(PrimitiveType.LONG))
		{
			return PrimitiveType.LONG;
		}
		else
		{
			return PrimitiveType.INT;
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

		PrimitiveType type = (PrimitiveType) this.getType(variables);
		PrimitiveType firstPrimitive = (PrimitiveType) firstType;
		PrimitiveType secondPrimitive = (PrimitiveType) secondType;

		result = result.concat(this.firstOperand.compile(variables));

		if (firstPrimitive != type)
		{
			result = result.add(new InsnNode(firstPrimitive.convertTo(type)));
		}

		result = result.concat(this.secondOperand.compile(variables));

		if (secondPrimitive != type)
		{
			result = result.add(new InsnNode(secondPrimitive.convertTo(type)));
		}

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
		Result result = this.firstOperand.compile(variables);

		Type firstOperandType = this.firstOperand.getType(variables);

		result = result.add(new MethodInsnNode(
			Opcodes.INVOKEVIRTUAL,
			"java/lang/StringBuilder",
			"append",
			"(" + firstOperandType.toJVMType() + ")Ljava/lang/StringBuilder;",
			false));

		result = result.concat(this.secondOperand.compile(variables));

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
