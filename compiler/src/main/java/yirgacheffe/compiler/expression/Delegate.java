package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Delegate implements Expression
{
	private Coordinate coordinate;

	private String thisType;

	private Array<Expression> arguments;

	public Delegate(Coordinate coordinate, String thisType, Array<Expression> arguments)
	{
		this.coordinate = coordinate;
		this.thisType = thisType;
		this.arguments = arguments;
	}

	public Type getType(Variables variables)
	{
		return PrimitiveType.VOID;
	}

	public Result compile(Variables variables)
	{
		Type type = this.getType(variables);

		variables.stackPush(type);

		if (this.arguments.length() != 1)
		{
			String message = "Delegate has one parameter.";

			return new Result().add(new Error(this.coordinate, message));
		}

		Result result = new Result();
		Expression argument = this.arguments.get(0);
		Type argumentType = argument.getType(variables);

		result = result
			.add(new VarInsnNode(Opcodes.ALOAD, 0))
			.concat(argument.compile(variables));

		if (argumentType.isAssignableTo(PrimitiveType.DOUBLE))
		{
			result = result.concat(argumentType.convertTo(PrimitiveType.DOUBLE));

			argumentType = PrimitiveType.DOUBLE;
		}

		result = result
			.add(new FieldInsnNode(
				Opcodes.PUTFIELD,
				this.thisType,
				"0delegate",
				"Ljava/lang/Object;"));

		variables.delegate(this, argumentType);

		return result;
	}

	public Result compileCondition(Variables variables, Label trueLabel, Label falseLabel)
	{
		return new Result();
	}

	public boolean isCondition(Variables variables)
	{
		return false;
	}

	public Array<VariableRead> getVariableReads()
	{
		return new Array<>();
	}

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}
}
