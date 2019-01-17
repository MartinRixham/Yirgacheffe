package yirgacheffe.compiler.listener;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.parser.YirgacheffeParser;

public class ParallelMethodListener extends MethodListener
{
	public ParallelMethodListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitParallelMethodDeclaration(
			YirgacheffeParser.ParallelMethodDeclarationContext context)
	{
		MethodVisitor methodVisitor = this.methodVisitor;

		String runnableClass =
			this.packageName + "/" +
			this.className + "$" +
			context.classMethodDeclaration().signature().Identifier();

		methodVisitor.visitTypeInsn(Opcodes.NEW, runnableClass);
		methodVisitor.visitInsn(Opcodes.DUP);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			runnableClass,
			"<init>",
			"()V",
			false);

		methodVisitor.visitVarInsn(Opcodes.ASTORE, 1);

		methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/Thread");
		methodVisitor.visitInsn(Opcodes.DUP);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/Thread",
			"<init>",
			"(Ljava/lang/Runnable;)V",
			false);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			"java/lang/Thread",
			"start",
			"()V",
			false);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
		methodVisitor.visitInsn(Opcodes.ARETURN);
	}
}
