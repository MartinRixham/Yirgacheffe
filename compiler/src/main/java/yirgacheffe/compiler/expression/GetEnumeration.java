package yirgacheffe.compiler.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.function.Interface;
import yirgacheffe.compiler.type.ConstantType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.variables.Variables;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Enumeration;
import yirgacheffe.lang.EnumerationWithDefault;

public class GetEnumeration implements Expression
{
	private Coordinate coordinate;

	private Type type;

	private Expression expression;

	public GetEnumeration(Coordinate coordinate, Type type, Expression expression)
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
		Result result = new Result();
		Type type = this.type;

		if (!this.isEnumeration(type))
		{
			String message = type + " is not an enumeration.";

			result = result.add(new Error(this.coordinate, message));
		}

		Type expressionType = this.expression.getType(variables);
		ConstantType constantType = new ConstantType(type);

		if (!constantType.matches(expressionType))
		{
			String message =
				"Expected enumeration constant of type " +
				constantType + " but found " + expressionType + ".";

			result = result.add(new Error(this.coordinate, message));
		}

		if (this.expression instanceof Literal)
		{
			Literal literal = (Literal) this.expression;
			String value = literal.getValue().toString();

			if (!type.reflect().hasMethod(value))
			{
				String message = "Unknown enumeration constant '" + value + "'.";

				result = result.add(new Error(this.coordinate, message));
			}

			result = result.add(new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				type.toFullyQualifiedType(),
				value,
				"()" + type.toJVMType(),
				this.type.reflect().isInterface()));
		}
		else
		{
			if (!this.hasDefault(type))
			{
				String message =
					"Cannot dynamically access enumeration without default constructor.";

				result = result
					.add(new Error(coordinate, message));
			}

			result = result
				.add(new FieldInsnNode(
					Opcodes.GETSTATIC,
					type.toFullyQualifiedType(),
					"values",
					"Ljava/util/Map;"))
				.concat(this.expression.compile(variables))
				.concat(expressionType.convertTo(new ReferenceType(Object.class)))
				.add(new MethodInsnNode(
					Opcodes.INVOKEINTERFACE,
					"java/util/Map",
					"get",
					"(Ljava/lang/Object;)Ljava/lang/Object;"
				))
				.concat(new ReferenceType(Object.class).convertTo(this.type));

			variables.stackPop();
		}

		variables.stackPush(type);

		return result;
	}

	private boolean isEnumeration(Type type)
	{
		Interface members = type.reflect();

		return members.doesImplement(Enumeration.class) ||
			members.doesImplement(EnumerationWithDefault.class);
	}

	private boolean hasDefault(Type type)
	{
		return type.reflect().doesImplement(EnumerationWithDefault.class);
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

	public Coordinate getCoordinate()
	{
		return this.coordinate;
	}
}
