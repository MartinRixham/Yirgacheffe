package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.function.Callable;
import yirgacheffe.compiler.type.GenericType;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class InvokeMethod implements Expression
{
	private Callable function;

	private Expression owner;

	private Expression[] arguments;

	public InvokeMethod(Callable function, Expression owner, Expression[] arguments)
	{
		this.function = function;
		this.owner = owner;
		this.arguments = arguments;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		this.owner.compile(methodVisitor);

		for (Expression expression: this.arguments)
		{
			expression.compile(methodVisitor);
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

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			this.withSlashes(owner),
			this.function.getName(),
			this.function.getDescriptor(),
			false);

		Type returnType = this.function.getReturnType();

		if (returnType instanceof GenericType)
		{
			methodVisitor.visitTypeInsn(
				Opcodes.CHECKCAST,
				this.withSlashes(returnType));
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
