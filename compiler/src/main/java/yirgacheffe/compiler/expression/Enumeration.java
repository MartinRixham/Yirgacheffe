package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;

public class Enumeration implements Expression
{
	private Coordinate coordinate;

	private Type type;

	private Expression expression;

	public Enumeration(Coordinate coordinate, Type type, Expression expression)
	{
		this.coordinate = coordinate;
		this.type = type;
		this.expression = expression;
	}

	public Type getType(Variables variables)
	{
		return this.type;
	}

	public Result compile(Variables variables)
	{
		Literal literal = (Literal) expression;
		String value = literal.getValue().toString();
		Result result = new Result();

		try
		{
			this.type.reflectionClass().getMethod(value);
		}
		catch (NoSuchMethodException e)
		{
			String message = "Unknown enumeration constant '" + value + "'.";

			result = result.add(new Error(coordinate, message));
		}

		result = result.add(new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			this.type.toFullyQualifiedType(),
			value,
			"()" + type.toJVMType()));

		variables.stackPush(this.type);

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

	public Array<VariableRead> getVariableReads()
	{
		return this.expression.getVariableReads();
	}
}
