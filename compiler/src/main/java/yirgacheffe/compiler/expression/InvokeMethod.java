package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.ReferenceType;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

public class InvokeMethod implements Expression
{
	private Callable function;

	private Expression owner;

	private Array<Expression> arguments;

	public InvokeMethod(Callable function, Expression owner, Array<Expression> arguments)
	{
		this.function = function;
		this.owner = owner;
		this.arguments = arguments;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		this.owner.compile(methodVisitor);

		Array<Type> parameters = this.function.getParameterTypes();

		for (int i = 0; i < this.arguments.length(); i++)
		{
			this.arguments.get(i).compile(methodVisitor);
			Type argumentType = this.arguments.get(i).getType();

			if (parameters.length() >= i + 1 &&
				argumentType instanceof PrimitiveType &&
				parameters.get(i) instanceof ReferenceType)
			{

				String descriptor =
					"(" + argumentType.toJVMType() + ")L" +
					this.withSlashes(argumentType) + ";";

				methodVisitor.visitMethodInsn(
					Opcodes.INVOKESTATIC,
					this.withSlashes(argumentType),
					"valueOf",
					descriptor,
					false);
			}
		}

		Type owner = this.function.getOwner();

		if (owner instanceof PrimitiveType)
		{
			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				this.withSlashes(owner),
				"valueOf",
				"(" + owner.toJVMType() + ")L" + this.withSlashes(owner) + ";",
				false);
		}

		boolean isInterface = owner.reflectionClass().isInterface();

		methodVisitor.visitMethodInsn(
			isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL,
			this.withSlashes(owner),
			this.function.getName(),
			this.function.getDescriptor(),
			isInterface);

		Type returnType = this.function.getReturnType();

		if (returnType instanceof GenericType)
		{
			methodVisitor.visitTypeInsn(
				Opcodes.CHECKCAST,
				this.withSlashes(returnType));

			Type type = ((GenericType) returnType).unwrap();

			if (type instanceof PrimitiveType)
			{
				methodVisitor.visitMethodInsn(
					Opcodes.INVOKESTATIC,
					"yirgacheffe/lang/Boxer",
					"ofValue",
					"(L" + this.withSlashes(type) + ";)" + type.toJVMType(),
					false);
			}
		}
		else if (returnType.equals(PrimitiveType.INT))
		{
			methodVisitor.visitInsn(Opcodes.I2D);
		}
		else if (returnType.equals(PrimitiveType.LONG))
		{
			methodVisitor.visitInsn(Opcodes.L2D);
		}
		else if (returnType.equals(PrimitiveType.FLOAT))
		{
			methodVisitor.visitInsn(Opcodes.F2D);
		}
	}

	@Override
	public Type getType()
	{
		return this.function.getReturnType();
	}

	private String withSlashes(Type type)
	{
		return type.toFullyQualifiedType().replace(".", "/");
	}
}
