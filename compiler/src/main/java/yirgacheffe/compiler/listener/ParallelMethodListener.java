package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.parser.YirgacheffeParser;

import java.lang.reflect.Method;

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

		String methodName =
			context.parallelMethodDeclaration().classMethodDeclaration()
				.signature().Identifier().getText();

		String runnableClass =
			this.packageName + "/" +
				this.className + "$" +
				methodName;

		this.compileRunMethod(writer, type, runnableClass, methodName);
		this.compileInterfaceMethods(writer, type, runnableClass);
		this.generatedClasses.push(writer.toByteArray());
	}

	private void compileRunMethod(
		ClassWriter writer,
		Type type,
		String runnableClass,
		String methodName)
	{
		MethodVisitor methodVisitor =
			writer.visitMethod(
				Opcodes.ACC_PUBLIC, "run", "()V", null, null);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

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
		methodVisitor.visitMaxs(0, 0);
	}

	private void compileInterfaceMethods(
		ClassWriter writer,
		Type type,
		String runnableClass)
	{
		Method[] interfaceMethods = type.reflectionClass().getMethods();

		for (Method method: interfaceMethods)
		{
			Function function = new Function(type, method);

			MethodVisitor methodVisitor =
				writer.visitMethod(
					Opcodes.ACC_PUBLIC,
					method.getName(),
					function.getDescriptor(),
					null,
					null);

			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitFieldInsn(
				Opcodes.GETFIELD,
				runnableClass,
				"0",
				type.toJVMType());

			Label label = new Label();

			methodVisitor.visitJumpInsn(Opcodes.IFNONNULL, label);
			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/Object",
				"wait",
				"()V",
				false);

			methodVisitor.visitLabel(label);

			methodVisitor.visitFieldInsn(
				Opcodes.GETFIELD,
				runnableClass,
				"0",
				type.toJVMType());

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				type.toFullyQualifiedType().replace(".", "/"),
				method.getName(),
				function.getDescriptor(),
				false);

			methodVisitor.visitInsn(type.getReturnInstruction());
			methodVisitor.visitMaxs(0, 0);
		}
	}
}
