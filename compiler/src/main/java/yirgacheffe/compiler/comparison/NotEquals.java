package yirgacheffe.compiler.comparison;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class NotEquals implements Comparison
{
	@Override
	public void compile(MethodVisitor methodVisitor, Label label, Type type)
	{
		if (type == PrimitiveType.DOUBLE)
		{
			methodVisitor.visitInsn(Opcodes.DCMPL);
			methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
		}
		else
		{
			methodVisitor.visitJumpInsn(Opcodes.IF_ICMPNE, label);
		}
	}
}
