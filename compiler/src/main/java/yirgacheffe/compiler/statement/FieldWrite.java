package yirgacheffe.compiler.statement;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.expression.Nothing;
import yirgacheffe.compiler.expression.VariableRead;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.compiler.type.Variables;
import yirgacheffe.lang.Array;

import java.lang.reflect.Field;

public class FieldWrite implements Statement
{
	private Coordinate coordinate;

	private String name;

	private Expression owner;

	private Expression value;

	public FieldWrite(
		Coordinate coordinate,
		String name,
		Expression owner,
		Expression value)
	{
		this.coordinate = coordinate;
		this.name = name;
		this.owner = owner;
		this.value = value;
	}

	public boolean returns()
	{
		return false;
	}

	public Result compile(Variables variables, Signature caller)
	{
		Type ownerType = this.owner.getType(variables);
		Type type = this.value.getType(variables);
		Result result = new Result();

		try
		{
			Field field = ownerType.reflectionClass().getDeclaredField(this.name);

			Class<?> fieldClass = field.getType();
			Class<?> expressionClass = type.reflectionClass();

			if (!fieldClass.isAssignableFrom(expressionClass) &&
				!fieldClass.getSimpleName()
					.equals(expressionClass.getSimpleName().toLowerCase()))
			{
				Type fieldType = this.getType(fieldClass);
				String message =
					"Cannot assign expression of type " + type +
					" to field of type " + fieldType + ".";

				result = result.add(new Error(this.coordinate, message));
			}
		}
		catch (NoSuchFieldException e)
		{
			String message = "Assignment to unknown field '" + this.name + "'.";

			result = result.add(new Error(this.coordinate, message));
		}

		return result
			.concat(this.owner.compile(variables))
			.concat(this.value.compile(variables))
			.add(new FieldInsnNode(
				Opcodes.PUTFIELD,
				ownerType.toFullyQualifiedType(),
				this.name,
				type.toJVMType()));
	}

	private Type getType(Class<?> clazz)
	{
		if (clazz.isPrimitive())
		{
			return PrimitiveType.valueOf(clazz.getName().toUpperCase());
		}
		else
		{
			return new ReferenceType(clazz);
		}
	}

	public Array<VariableRead> getVariableReads()
	{
		return this.owner.getVariableReads()
			.concat(this.value.getVariableReads());
	}

	public Array<VariableWrite> getVariableWrites()
	{
		return new Array<>();
	}

	public Expression getExpression()
	{
		return new Nothing();
	}

	public boolean isEmpty()
	{
		return false;
	}
}
