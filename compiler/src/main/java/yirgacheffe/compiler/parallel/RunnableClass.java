package yirgacheffe.compiler.parallel;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

		classNode.methods.add(this.compileRunMethod());
		classNode.methods.addAll(this.compileInterfaceMethods());
		classNode.methods.add(this.compileConstructor());

		ClassWriter writer =
			new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

		classNode.accept(writer);

		return new GeneratedClass(this.className, writer.toByteArray());
	}

	private MethodNode compileRunMethod()
	{
		MethodNode methodNode =
			new MethodNode(
				Opcodes.ACC_PUBLIC, "run", "()V", null, null);

		Label start = new Label();
		Label end = new Label();
		Label handler = new Label();

		methodNode.visitTryCatchBlock(start, end, handler, "java/lang/Throwable");

		methodNode.visitLabel(start);

		methodNode.visitVarInsn(Opcodes.ALOAD, 0);
		methodNode.visitInsn(Opcodes.DUP);

		methodNode.visitFieldInsn(
			Opcodes.GETFIELD,
			this.className,
			"0",
			"L" + this.parentClassName + ";");

		Array<Type> parameters = this.signature.getParameters();
		StringBuilder descriptor = new StringBuilder("(L" + this.parentClassName + ";");

		for (int i = 0; i < parameters.length(); i++)
		{
			methodNode.visitVarInsn(Opcodes.ALOAD, 0);

			methodNode.visitFieldInsn(
				Opcodes.GETFIELD,
				this.className,
				i + 1 + "",
				parameters.get(i).toJVMType());

			descriptor.append(parameters.get(i).toJVMType());
		}

		methodNode.visitMethodInsn(
			Opcodes.INVOKESTATIC,
			this.className,
			this.methodName,
			descriptor.toString() + ")" + this.type.toJVMType(),
			false);

		methodNode.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			"0return",
			this.type.toJVMType());

		methodNode.visitLabel(end);

		Label label = new Label();

		methodNode.visitJumpInsn(Opcodes.GOTO, label);

		methodNode.visitLabel(handler);

		methodNode.visitVarInsn(Opcodes.ALOAD, 0);
		methodNode.visitInsn(Opcodes.SWAP);

		methodNode.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			"0exception",
			"Ljava/lang/Throwable;");

		methodNode.visitLabel(label);

		methodNode.visitVarInsn(Opcodes.ALOAD, 0);
		methodNode.visitInsn(Opcodes.ICONST_1);

		methodNode.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			"0ran",
			"Z");

		methodNode.visitVarInsn(Opcodes.ALOAD, 0);
		methodNode.visitInsn(Opcodes.MONITORENTER);
		methodNode.visitVarInsn(Opcodes.ALOAD, 0);

		methodNode.visitMethodInsn(
			Opcodes.INVOKEVIRTUAL,
			"java/lang/Object",
			"notifyAll",
			"()V",
			false);

		methodNode.visitVarInsn(Opcodes.ALOAD, 0);
		methodNode.visitInsn(Opcodes.MONITOREXIT);

		methodNode.visitInsn(Opcodes.RETURN);

		return methodNode;
	}

	private List<MethodNode> compileInterfaceMethods()
	{
		List<MethodNode> methods = new ArrayList<>();
		Set<Function> interfaceMethods = this.type.reflect().getPublicMethods();

		for (Function method: interfaceMethods)
		{
			MethodNode methodNode =
				new MethodNode(
					Opcodes.ACC_PUBLIC,
					method.getName(),
					method.getDescriptor(),
					null,
					null);

			methods.add(methodNode);

			methodNode.visitVarInsn(Opcodes.ALOAD, 0);

			methodNode.visitFieldInsn(
				Opcodes.GETFIELD,
				this.className,
				"0ran",
				"Z");

			Label label = new Label();

			methodNode.visitJumpInsn(Opcodes.IFNE, label);
			methodNode.visitVarInsn(Opcodes.ALOAD, 0);
			methodNode.visitInsn(Opcodes.MONITORENTER);
			methodNode.visitVarInsn(Opcodes.ALOAD, 0);

			methodNode.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				"java/lang/Object",
				"wait",
				"()V",
				false);

			methodNode.visitVarInsn(Opcodes.ALOAD, 0);
			methodNode.visitInsn(Opcodes.MONITOREXIT);

			methodNode.visitLabel(label);

			methodNode.visitVarInsn(Opcodes.ALOAD, 0);

			methodNode.visitFieldInsn(
				Opcodes.GETFIELD,
				this.className,
				"0exception",
				"Ljava/lang/Throwable;");

			Label secondExceptionLabel = new Label();

			methodNode.visitJumpInsn(Opcodes.IFNULL, secondExceptionLabel);

			methodNode.visitVarInsn(Opcodes.ALOAD, 0);

			methodNode.visitFieldInsn(
				Opcodes.GETFIELD,
				this.className,
				"0exception",
				"Ljava/lang/Throwable;");

			methodNode.visitInsn(Opcodes.ATHROW);

			methodNode.visitLabel(secondExceptionLabel);

			methodNode.visitVarInsn(Opcodes.ALOAD, 0);

			methodNode.visitFieldInsn(
				Opcodes.GETFIELD,
				this.className,
				"0return",
				this.type.toJVMType());

			Array<Type> parameters = method.getParameterTypes();

			for (int i = 0; i < parameters.length(); i++)
			{
				methodNode.visitVarInsn(
					parameters.get(i).getLoadInstruction(), i + 1);
			}

			methodNode.visitMethodInsn(
				Opcodes.INVOKEINTERFACE,
				this.type.toFullyQualifiedType(),
				method.getName(),
				method.getDescriptor(),
				true);

			methodNode.visitInsn(method.getReturnType().getReturnInstruction());
		}

		return methods;
	}

	private MethodNode compileConstructor()
	{
		Array<Type> parameters = this.signature.getParameters();
		StringBuilder descriptor = new StringBuilder("(L" + this.parentClassName + ";");

		for (int i = 0; i < parameters.length(); i++)
		{
			descriptor.append(parameters.get(i).toJVMType());
		}

		MethodNode methodNode =
			new MethodNode(
				Opcodes.ACC_PUBLIC,
				"<init>",
				descriptor.toString() + ")V",
				null,
				null);

		methodNode.visitVarInsn(Opcodes.ALOAD, 0);

		methodNode.visitMethodInsn(
			Opcodes.INVOKESPECIAL,
			"java/lang/Object",
			"<init>",
			"()V",
			false);

		methodNode.visitVarInsn(Opcodes.ALOAD, 0);
		methodNode.visitVarInsn(Opcodes.ALOAD, 1);

		methodNode.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			"0",
			"L" + this.parentClassName + ";");

		for (int i = 0; i < parameters.length(); i++)
		{
			methodNode.visitVarInsn(Opcodes.ALOAD, 0);
			methodNode.visitVarInsn(parameters.get(i).getLoadInstruction(), i + 2);

			methodNode.visitFieldInsn(
				Opcodes.PUTFIELD,
				this.className,
				i + 1 + "",
				parameters.get(i).toJVMType());
		}

		methodNode.visitInsn(Opcodes.RETURN);

		return methodNode;
	}
}
