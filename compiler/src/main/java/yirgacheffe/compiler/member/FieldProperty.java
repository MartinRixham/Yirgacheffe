package yirgacheffe.compiler.member;

import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldProperty implements Property
{
	private Type owner;

	private Field field;

	public FieldProperty(Type owner, Field field)
	{
		this.owner = owner;
		this.field = field;
	}

	public String getName()
	{
		return this.field.getName();
	}

	public Result checkType(Coordinate coordinate, Type type)
	{
		Class<?> fieldClass = field.getType();
		Interface expressionClass = type.reflect();

		if (expressionClass.doesImplement(fieldClass) ||
			fieldClass.getSimpleName()
			.equals(expressionClass.getSimpleName().toLowerCase()))
		{
			return new Result();
		}
		else
		{
			Type fieldType = this.getType(fieldClass);
			String message =
				"Cannot assign expression of type " + type +
					" to field of type " + fieldType + ".";

			return new Result().add(new Error(coordinate, message));
		}
	}

	public boolean isStatic()
	{
		return Modifier.isStatic(this.field.getModifiers());
	}

	public Type getType()
	{
		return Type.getType(this.field.getGenericType(), this.owner);
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
}
