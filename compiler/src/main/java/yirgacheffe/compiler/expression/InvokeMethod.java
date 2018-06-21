package yirgacheffe.compiler.expression;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.PrimitiveType;
import yirgacheffe.compiler.type.Type;

public class InvokeMethod implements Expression
{
	private Type owner;

	private String name;

	private String descriptor;

	private Type returnType;

	public InvokeMethod(Type owner, String name, String descriptor, Type returnType)
	{
		this.owner = owner;
		this.name = name;
		this.descriptor = descriptor;
		this.returnType = returnType;
	}

	@Override
	public void compile(MethodVisitor methodVisitor)
	{
		if (this.owner instanceof PrimitiveType)
		{
			String typeWithSlashes =
				this.owner.toFullyQualifiedType().replace(".", "/");

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				typeWithSlashes,
				"valueOf",
				"(" + this.owner.toJVMType() + ")L" + typeWithSlashes + ";",
				false);
		}

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			this.owner.toFullyQualifiedType().replace(".", "/"),
			this.name,
			this.descriptor,
			false);

		if (this.returnType.equals(PrimitiveType.INT))
		{
			methodVisitor.visitInsn(Opcodes.I2D);
		}
		else if (this.returnType.equals(PrimitiveType.LONG))
		{
			methodVisitor.visitInsn(Opcodes.L2D);
		}
		else if (this.returnType.equals(PrimitiveType.FLOAT))
		{
			methodVisitor.visitInsn(Opcodes.F2D);
		}
	}
}
