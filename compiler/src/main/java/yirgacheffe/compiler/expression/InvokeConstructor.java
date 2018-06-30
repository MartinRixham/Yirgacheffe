package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.function.Callable;

public class InvokeConstructor implements Expression
{
	private Callable function;

	private Expression[] arguments;

	public InvokeConstructor(Callable function, Expression[] arguments)
	{
		this.function = function;
		this.arguments = arguments;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		String typeWithSlashes =
			this.function.getOwner().toFullyQualifiedType().replace(".", "/");

		methodVisitor.visitTypeInsn(Opcodes.NEW, typeWithSlashes);
		methodVisitor.visitInsn(Opcodes.DUP);

		for (Expression expression: this.arguments)
		{
			expression.compile(methodVisitor);
		}

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			typeWithSlashes,
			"<init>",
			this.function.getDescriptor(),
			false);
	}
}
