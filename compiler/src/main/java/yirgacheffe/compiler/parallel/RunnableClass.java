package yirgacheffe.compiler.parallel;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.function.Function;
import yirgacheffe.compiler.function.Signature;
import yirgacheffe.compiler.type.Type;
import yirgacheffe.lang.Array;

import java.lang.reflect.Method;

public class RunnableClass
{
	private String className;

	private String methodName;

	private Type type;

	private Signature signature;

	public RunnableClass(
		String className,
		String methodName,
		Type type,
		Signature signature)
	{
		this.className = className;
		this.methodName = methodName;
		this.type = type;
		this.signature = signature;
	}

	public GeneratedClass compile(ClassWriter writer)
	{
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

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitInsn(Opcodes.DUP);

		Array<Type> parameters = this.signature.getParameters();
		StringBuilder descriptor = new StringBuilder("(");

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
			Opcodes.INVOKEVIRTUAL,
			this.className,
			this.methodName,
			descriptor.toString() + ")" + this.type.toJVMType(),
			false);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD,
			this.className,
			"0",
			this.type.toJVMType());

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
				"0",
				this.type.toJVMType());

			Label label = new Label();

			methodVisitor.visitJumpInsn(Opcodes.IFNONNULL, label);
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
				"0",
				this.type.toJVMType());

			Array<Type> parameters = function.getParameterTypes();

			for (int i = 0; i < parameters.length(); i++)
			{
				methodVisitor.visitVarInsn(
					parameters.get(i).getLoadInstruction(), i + 1);
			}

			methodVisitor.visitMethodInsn(
				Opcodes.INVOKEINTERFACE,
				this.type.toFullyQualifiedType().replace(".", "/"),
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
		StringBuilder descriptor = new StringBuilder("(");

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

		for (int i = 0; i < parameters.length(); i++)
		{
			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
			methodVisitor.visitVarInsn(parameters.get(i).getLoadInstruction(), i + 1);

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
