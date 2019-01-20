package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

public class ParallelMethodListener extends MethodListener
{
	private ClassWriter generatedClassWriter;

	public ParallelMethodListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitParallelMethodDeclaration(
			YirgacheffeParser.ParallelMethodDeclarationContext context)
	{
		MethodVisitor methodVisitor = this.methodVisitor;

		String methodName =
			context.classMethodDeclaration().signature().Identifier().getText();

		String runnableClass =
			this.packageName + "/" +
			this.className + "$" +
			methodName;

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

		ClassWriter writer =
			new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

		writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
			runnableClass,
			null,
			"java/lang/Object",
			new String[] {"java/lang/Runnable", "java/lang/Comparable"});

		this.methodVisitor =
			writer.visitMethod(
				Opcodes.ACC_PRIVATE,
				methodName,
				"()V",
				null,
				null);

		this.generatedClassWriter = writer;
	}

	@Override
	public void exitParallelMethod(YirgacheffeParser.ParallelMethodContext context)
	{
		ClassWriter writer = this.generatedClassWriter;

		YirgacheffeParser.TypeContext typeContext =
			context.parallelMethodDeclaration().classMethodDeclaration().type();

		Type type = this.types.getType(typeContext);

		writer.visitField(
			Opcodes.ACC_PUBLIC, "0", type.toJVMType(), null, null);

		this.compileRunMethod(writer, context, type);

		this.generatedClasses.push(writer.toByteArray());
	}

	private void compileRunMethod(
		ClassWriter writer,
		YirgacheffeParser.ParallelMethodContext context,
		Type type)
	{
		MethodVisitor methodVisitor =
			writer.visitMethod(
				Opcodes.ACC_PUBLIC, "run", "()V", null, null);

		String methodName =
			context.parallelMethodDeclaration().classMethodDeclaration()
				.signature().Identifier().getText();

		String runnableClass =
			this.packageName + "/" +
				this.className + "$" +
				methodName;

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			runnableClass,
			methodName,
			"()" + type.toJVMType(),
			false);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			runnableClass,
			"0",
			type.toJVMType());

		methodVisitor.visitInsn(Opcodes.RETURN);
	}
}
