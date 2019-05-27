package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class LessThan implements Comparator
{
	@Override
	public void compile(MethodVisitor methodVisitor, Label label, Type type)
	{
		if (type == PrimitiveType.DOUBLE)
		{
			methodVisitor.visitInsn(Opcodes.DCMPG);
			methodVisitor.visitJumpInsn(Opcodes.IFGE, label);
		}
		else if (type == PrimitiveType.LONG)
		{
			methodVisitor.visitInsn(Opcodes.LCMP);
			methodVisitor.visitJumpInsn(Opcodes.IFGE, label);
		}
		else
		{
			methodVisitor.visitJumpInsn(Opcodes.IF_ICMPGE, label);
		}
	}
}
