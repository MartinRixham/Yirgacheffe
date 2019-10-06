package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.NullType;
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
		Type type = this.type;

		if (!this.isEnumeration(type))
		{
			String message = type + " is not an enumeration.";

			result = result.add(new Error(this.coordinate, message));
		}

		Type literalType = literal.getType(variables);
		Type constantType = this.getConstantType(type);

		if (!literalType.isAssignableTo(constantType))
		{
			String message =
				"Expected enumeration constant of type " +
				constantType + " but found " + literalType + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		try
		{
			type.reflectionClass().getMethod(value);
		}
		catch (NoSuchMethodException e)
		{
			String message = "Unknown enumeration constant '" + value + "'.";

			result = result.add(new Error(this.coordinate, message));
		}

		result = result.add(new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			type.toFullyQualifiedType(),
			value,
			"()" + type.toJVMType()));

		variables.stackPush(type);

		return result;
	}

	private boolean isEnumeration(Type type)
	{
		return yirgacheffe.lang.Enumeration.class.isAssignableFrom(
			type.reflectionClass());
	}

	private Type getConstantType(Type type)
	{
		java.lang.reflect.Type[] interfaces =
			type.reflectionClass().getGenericInterfaces();

		for (java.lang.reflect.Type interfaceType: interfaces)
		{
			if (interfaceType.getTypeName().startsWith("yirgacheffe.lang.Enumeration"))
			{
				return Type.getType(interfaceType, type).getTypeParameter("T");
			}
		}

		return new NullType();
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