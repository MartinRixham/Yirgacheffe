package yirgacheffe.compiler.listener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.parallel.RunnableClass;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;
import yirgacheffe.parser.YirgacheffeParser;

public class ParallelMethodListener extends FieldDeclarationListener
{
	private ClassNode generatedClassWriter;

	public ParallelMethodListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void exitParallelMethodDeclaration(
		YirgacheffeParser.ParallelMethodDeclarationContext context)
	{
		if (!this.returnType.reflectionClass().isInterface())
		{
			return;
		}

		MethodNode methodVisitor = this.methodNode;

		String methodName =
			context.classMethodDeclaration().signature().Identifier().getText();

		String runnableClass = this.className + "$" + methodName;

		methodVisitor.visitTypeInsn(Opcodes.NEW, runnableClass);
		methodVisitor.visitInsn(Opcodes.DUP);

		Array<Type> parameters = this.signature.getParameters();
		StringBuilder descriptor = new StringBuilder("(L" + this.className + ";");

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

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

		ClassNode writer = new ClassNode();

		writer.visit(
			Opcodes.V1_8,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
			runnableClass,
			null,
			"java/lang/Object",
			new String[] {"java/lang/Runnable", this.returnType.toFullyQualifiedType()});

		String signature =
			this.signature
				.getDescriptor()
				.replace("(", "(L" + className + ";");

		this.methodNode =
			new MethodNode(
				Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
				methodName,
				signature,
				null,
				null);

		writer.methods.add(this.methodNode);

		this.generatedClassWriter = writer;
	}

	@Override
	public void exitParallelMethod(YirgacheffeParser.ParallelMethodContext context)
	{
		if (!this.returnType.reflectionClass().isInterface())
		{
			String message = "Parallel method must have interface return type.";

			this.errors.push(new Error(context, message));

			return;
		}

		String methodName =
			context.parallelMethodDeclaration().classMethodDeclaration()
				.signature().Identifier().getText();

		RunnableClass runnableClass =
			new RunnableClass(
				this.sourceFile,
				this.className,
				this.className + "$" + methodName,
				methodName,
				this.returnType,
				this.signature);

		this.generatedClasses.push(runnableClass.compile(this.generatedClassWriter));
	}
}
