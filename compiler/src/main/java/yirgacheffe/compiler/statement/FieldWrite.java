package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
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
import java.lang.reflect.Modifier;

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

	public Array<Error> compile(
		MethodVisitor methodVisitor,
		Variables variables,
		Signature caller)
	{
		Type ownerType = this.owner.getType(variables);
		Type type = this.value.getType(variables);
		Array<Error> errors = new Array<>();

		try
		{
			Field field = ownerType.reflectionClass().getDeclaredField(this.name);

			if (Modifier.isFinal(field.getModifiers()))
			{
				String message = "Cannot assign to constant " + this.name + ".";

				errors.push(new Error(this.coordinate, message));
			}

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

				errors.push(new Error(this.coordinate, message));
			}
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}

		errors.push(this.owner.compile(methodVisitor, variables));
		errors.push(this.value.compile(methodVisitor, variables));

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			ownerType.toFullyQualifiedType(),
			this.name,
			type.toJVMType());

		return errors;
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
