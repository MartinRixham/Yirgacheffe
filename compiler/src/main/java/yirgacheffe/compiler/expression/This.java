package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class This implements Expression
{
	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
	}

	@Override
	public int getStackHeight()
	{
		return 1;
	}
}
