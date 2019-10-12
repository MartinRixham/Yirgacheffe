package yirgacheffe.compiler.generated;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class MainMethod
{
	private String className;

	private String mainMethodName;

	public MainMethod(String className, String mainMethodName)
	{
		this.className = className;
		this.mainMethodName = mainMethodName;
	}

	public MethodNode generate()
	{
		MethodNode method =
			new MethodNode(
				Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
				"main",
				"([Ljava/lang/String;)V",
				null,
				null);

		method.visitTypeInsn(Opcodes.NEW, this.className);
		method.visitInsn(Opcodes.DUP);

		method.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			this.className,
			"<init>",
			"()V",
			false);

		method.visitTypeInsn(Opcodes.NEW, "yirgacheffe/lang/Array");
		method.visitInsn(Opcodes.DUP);
		method.visitVarInsn(Opcodes.ALOAD, 0);

		method.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"yirgacheffe/lang/Array",
			"<init>",
			"([Ljava/lang/Object;)V",
			false);

		method.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			this.className,
			this.mainMethodName,
			"(Lyirgacheffe/lang/Array;)V",
			false);

		method.visitInsn(Opcodes.RETURN);

		return method;
	}
}
