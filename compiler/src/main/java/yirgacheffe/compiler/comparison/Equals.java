package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class Equals implements Comparison
{
	@Override
	public void compile(MethodVisitor methodVisitor, Label label, Type type)
	{
		if (type == PrimitiveType.DOUBLE)
		{
			methodVisitor.visitInsn(Opcodes.DCMPL);
			methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
		}
		else if (type == PrimitiveType.LONG)
		{
			methodVisitor.visitInsn(Opcodes.LCMP);
			methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
		}
		else
		{
			methodVisitor.visitJumpInsn(Opcodes.IF_ICMPNE, label);
		}
	}
}
