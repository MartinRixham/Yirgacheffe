package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.function.Callable;

public class InvokeConstructor implements Expression
{
	private Callable function;

	public InvokeConstructor(Callable function)
	{
		this.function = function;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			this.function.getOwner().toFullyQualifiedType().replace(".", "/"),
			"<init>",
			this.function.getDescriptor(),
			false);
	}
}
