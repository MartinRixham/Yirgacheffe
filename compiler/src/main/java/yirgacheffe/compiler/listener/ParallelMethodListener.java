package yirgacheffe.compiler.listener;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.parallel.RunnableClass;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
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

		String packageName = this.packageName == null ? "" : this.packageName + "/";
		String runnableClass = packageName + this.className + "$" + methodName;

		methodVisitor.visitTypeInsn(Opcodes.NEW, runnableClass);
		methodVisitor.visitInsn(Opcodes.DUP);

		Array<Type> parameters = this.signature.getParameters();
		StringBuilder descriptor = new StringBuilder("(");

		for (int i = 0; i < parameters.length(); i++)
		{
			methodVisitor.visitVarInsn(parameters.get(i).getLoadInstruction(), i + 1);
			descriptor.append(parameters.get(i).toJVMType());
		}

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			runnableClass,
			"<init>",
			descriptor.toString() + ")V",
			false);

		methodVisitor.visitVarInsn(Opcodes.ASTORE, parameters.length() + 1);

		methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/Thread");
		methodVisitor.visitInsn(Opcodes.DUP);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, parameters.length() + 1);

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

		methodVisitor.visitVarInsn(Opcodes.ALOAD, parameters.length() + 1);
		methodVisitor.visitInsn(Opcodes.ARETURN);
		methodVisitor.visitMaxs(0, 0);

		ClassWriter writer =
			new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		String returnType = this.returnType.toFullyQualifiedType().replace(".", "/");

		writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
			runnableClass,
			null,
			"java/lang/Object",
			new String[] {"java/lang/Runnable", returnType});

		this.methodVisitor =
			writer.visitMethod(
				Opcodes.ACC_PRIVATE,
				methodName,
				this.signature.getDescriptor(),
				this.signature.getSignature(),
				null);

		this.generatedClassWriter = writer;
	}

	@Override
	public void exitParallelMethod(YirgacheffeParser.ParallelMethodContext context)
	{
		ClassWriter writer = this.generatedClassWriter;

		YirgacheffeParser.TypeContext typeContext =
			context.parallelMethodDeclaration()
				.classMethodDeclaration()
				.returnType()
				.type();

		Type type = this.types.getType(typeContext);

		writer.visitField(
			Opcodes.ACC_PRIVATE, "0", type.toJVMType(), null, null);

		Array<Type> parameters = this.signature.getParameters();

		for (int i = 0; i < parameters.length(); i++)
		{
			String name = 1 + i + "";

			writer.visitField(
				Opcodes.ACC_PRIVATE, name, parameters.get(i).toJVMType(), null, null);
		}

		String methodName =
			context.parallelMethodDeclaration().classMethodDeclaration()
				.signature().Identifier().getText();

		String packageName = this.packageName == null ? "" : this.packageName + "/";
		String className = packageName + this.className + "$" + methodName;

		RunnableClass runnableClass =
			new RunnableClass(className, methodName, type, this.signature);

		this.generatedClasses.push(runnableClass.compile(writer));
	}
}
