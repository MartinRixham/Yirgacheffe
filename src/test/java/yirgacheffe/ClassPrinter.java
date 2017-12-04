package yirgacheffe;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassPrinter extends ClassVisitor
{
	private byte[] cl;

	private String printedClass = "";

	public static void main(String[] args) throws Exception
	{
		ClassPrinter classPrinter = new ClassPrinter(writeClass());

		System.out.println(classPrinter.print());
	}

	private static byte[] writeClass()
	{
		ClassWriter cw = new ClassWriter(0);

		cw.visit(
			Opcodes.V1_5,
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
			"pkg/Comparable",
			null,
			"java/lang/Object",
			new String[] {"pkg/Mesurable"});

		cw.visitField(
			Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
			"LESS",
			"I",
			null,
			new Integer(-1))
			.visitEnd();

		cw.visitField(
			Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
			"EQUAL",
			"I",
			null,
			new Integer(0))
			.visitEnd();

		cw.visitField(
			Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
			"GREATER",
			"I",
			null,
			new Integer(1))
			.visitEnd();

		cw.visitMethod(
			Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
			"compareTo",
			"(Ljava/lang/Object;)I",
			null,
			null)
			.visitEnd();

		return cw.toByteArray();
	}

	public ClassPrinter(byte[] cl)
	{
		super(Opcodes.ASM4);

		this.cl = cl;
	}

	public String print()
	{
		ClassReader cr = new ClassReader(this.cl);
		cr.accept(this, 0);

		return this.printedClass;
	}

	@Override
	public void visit(
		int version,
		int access,
		String name,
		String signature,
		String superName,
		String[] interfaces)
	{
		this.printedClass += name + " extends " + superName + " {\n";
	}

	@Override
	public void visitSource(String source, String debug)
	{
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc)
	{
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		return null;
	}

	@Override
	public void visitAttribute(Attribute attr)
	{
	}

	@Override
	public void visitInnerClass(
		String name,
		String outerName,
		String innerName,
		int access)
	{
	}

	@Override
	public FieldVisitor visitField(
		int access,
		String name,
		String desc,
		String signature,
		Object value)
	{
		this.printedClass += "    " + desc + " " + name + "\n";

		return null;
	}

	@Override
	public MethodVisitor visitMethod(
		int access,
		String name,
		String desc,
		String signature,
		String[] exceptions)
	{
		this.printedClass += "    " + name + desc + "\n";

		return null;
	}

	@Override
	public void visitEnd()
	{
		this.printedClass += "}\n";
	}
}
