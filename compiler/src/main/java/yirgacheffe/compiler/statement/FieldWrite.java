package yirgacheffe.compiler.statement;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.error.ErrorMessage;
import yirgacheffe.compiler.error.FieldAssignmentError;
import yirgacheffe.compiler.expression.Expression;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;

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

	@Override
	public void compile(MethodVisitor methodVisitor, StatementResult result)
	{
		Type ownerType = this.owner.check(result);
		Type type = this.value.check(result);

		try
		{
			Class<?> fieldClass =
				ownerType.reflectionClass().getDeclaredField(this.name).getType();
			Class<?> expressionClass = type.reflectionClass();

			if (!fieldClass.isAssignableFrom(expressionClass) &&
				!fieldClass.getSimpleName()
					.equals(expressionClass.getSimpleName().toLowerCase()))
			{
				Type fieldType = this.getType(fieldClass);
				ErrorMessage message = new FieldAssignmentError(fieldType, type);

				result.error(new Error(this.coordinate, message));
			}
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}

		this.owner.compile(methodVisitor);
		this.value.compile(methodVisitor);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			ownerType.toFullyQualifiedType(),
			this.name,
			type.toJVMType());
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
