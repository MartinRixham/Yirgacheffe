package yirgacheffe.compiler.parallel;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Method;

public class RunnableClass
{
	private String sourceFile;

	private String parentClassName;

	private String className;

	private String methodName;

	private Type type;

	private Signature signature;

	public RunnableClass(
		String sourceFile,
		String parentClassName,
		String className,
		String methodName,
		Type type,
		Signature signature)
	{
		this.sourceFile = sourceFile;
		this.parentClassName = parentClassName;
		this.className = className;
		this.methodName = methodName;
		this.type = type;
		this.signature = signature;
	}

	public GeneratedClass compile(ClassNode classNode)
	{
		classNode.visitSource(this.sourceFile, null);

		classNode.visitField(
			Opcodes.ACC_PRIVATE, "0exception", "Ljava/lang/Throwable;", null, null);

		classNode.visitField(
			Opcodes.ACC_PRIVATE, "0ran", "Z", null, null);

		classNode.visitField(
			Opcodes.ACC_PRIVATE, "0return", this.type.toJVMType(), null, null);

		classNode.visitField(
			Opcodes.ACC_PRIVATE, "0", "L" + this.parentClassName + ";", null, null);

		Array<Type> parameters = this.signature.getParameters();

		for (int i = 0; i < parameters.length(); i++)
		{
			String name = 1 + i + "";

			classNode.visitField(
				Opcodes.ACC_PRIVATE, name, parameters.get(i).toJVMType(), null, null);
		}

		ClassWriter writer =
			new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

		classNode.accept(writer);

		this.compileRunMethod(writer);
		this.compileInterfaceMethods(writer);
		this.compileConstructor(writer);

		return new GeneratedClass(this.className, writer.toByteArray());
	}

	private void compileRunMethod(ClassWriter writer)
	{
		MethodVisitor methodVisitor =
			writer.visitMethod(
				Opcodes.ACC_PUBLIC, "run", "()V", null, null);

		Label start = new Label();
		Label end = new Label();
		Label handler = new Label();

		methodVisitor.visitTryCatchBlock(start, end, handler, "java/lang/Throwable");

		methodVisitor.visitLabel(start);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitInsn(Opcodes.DUP);

		methodVisitor.visitFieldInsn(
			Opcodes.GETFIELD,
			this.className,
			"0",
			"L" + this.parentClassName + ";");

		Array<Type> parameters = this.signature.getParameters();
		StringBuilder descriptor = new StringBuilder("(L" + this.parentClassName + ";");

		for (int i = 0; i < parameters.length(); i++)
		{
			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitFieldInsn(
				Opcodes.GETFIELD,
				this.className,
				i + 1 + "",
				parameters.get(i).toJVMType());

			descriptor.append(parameters.get(i).toJVMType());
		}

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESTATIC,
			this.className,
			this.methodName,
			descriptor.toString() + ")" + this.type.toJVMType(),
			false);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			"0return",
			this.type.toJVMType());

		methodVisitor.visitLabel(end);

		Label label = new Label();

		methodVisitor.visitJumpInsn(Opcodes.GOTO, label);

		methodVisitor.visitLabel(handler);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitInsn(Opcodes.SWAP);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			"0exception",
			"Ljava/lang/Throwable;");

		methodVisitor.visitLabel(label);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitInsn(Opcodes.ICONST_1);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			"0ran",
			"Z");

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitInsn(Opcodes.MONITORENTER);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			"java/lang/Object",
			"notifyAll",
			"()V",
			false);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitInsn(Opcodes.MONITOREXIT);

		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(0, 0);
	}

	private void compileInterfaceMethods(ClassWriter writer)
	{
		Method[] interfaceMethods = this.type.reflectionClass().getMethods();

		for (Method method: interfaceMethods)
		{
			Function function = new Function(this.type, method);

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
				this.className,
				"0ran",
				"Z");

			Label label = new Label();

			methodVisitor.visitJumpInsn(Opcodes.IFNE, label);
			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
			methodVisitor.visitInsn(Opcodes.MONITORENTER);
			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/Object",
				"wait",
				"()V",
				false);

			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
			methodVisitor.visitInsn(Opcodes.MONITOREXIT);

			methodVisitor.visitLabel(label);

			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitFieldInsn(
				Opcodes.GETFIELD,
				this.className,
				"0exception",
				"Ljava/lang/Throwable;");

			Label secondExceptionLabel = new Label();

			methodVisitor.visitJumpInsn(Opcodes.IFNULL, secondExceptionLabel);

			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitFieldInsn(
				Opcodes.GETFIELD,
				this.className,
				"0exception",
				"Ljava/lang/Throwable;");

			methodVisitor.visitInsn(Opcodes.ATHROW);

			methodVisitor.visitLabel(secondExceptionLabel);

			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitFieldInsn(
				Opcodes.GETFIELD,
				this.className,
				"0return",
				this.type.toJVMType());

			Array<Type> parameters = function.getParameterTypes();

			for (int i = 0; i < parameters.length(); i++)
			{
				methodVisitor.visitVarInsn(
					parameters.get(i).getLoadInstruction(), i + 1);
			}

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKEINTERFACE,
				this.type.toFullyQualifiedType(),
				method.getName(),
				function.getDescriptor(),
				true);

			methodVisitor.visitInsn(function.getReturnType().getReturnInstruction());
			methodVisitor.visitMaxs(0, 0);
		}
	}

	private void compileConstructor(ClassWriter writer)
	{
		Array<Type> parameters = this.signature.getParameters();
		StringBuilder descriptor = new StringBuilder("(L" + this.parentClassName + ";");

		for (int i = 0; i < parameters.length(); i++)
		{
			descriptor.append(parameters.get(i).toJVMType());
		}

		MethodVisitor methodVisitor =
			writer.visitMethod(
				Opcodes.ACC_PUBLIC,
				"<init>",
				descriptor.toString() + ")V",
				null,
				null);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/Object",
			"<init>",
			"()V",
			false);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			"0",
			"L" + this.parentClassName + ";");

		for (int i = 0; i < parameters.length(); i++)
		{
			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
			methodVisitor.visitVarInsn(parameters.get(i).getLoadInstruction(), i + 2);

			methodVisitor.visitFieldInsn(
				Opcodes.PUTFIELD,
				this.className,
				i + 1 + "",
				parameters.get(i).toJVMType());
		}

		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(0, 0);
	}
}
