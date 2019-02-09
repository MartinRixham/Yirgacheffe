package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.parallel.GeneratedClass;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParallelMethodListenerTest
{
	@Test
	public void testParallelMethod()
	{
		String source =
			"package myPackage;\n" +
			"class MyClass\n" +
			"{\n" +
				"parallel public Comparable<String> method(String thingy)" +
				"{\n" +
					"return thingy;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("myPackage/MyClass.yg", source);

		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		InsnList instructions = firstMethod.instructions;

		assertEquals("(Ljava/lang/String;)Ljava/lang/Comparable;", firstMethod.desc);
		assertEquals(3, firstMethod.maxLocals);
		assertEquals(3, firstMethod.maxStack);
		assertEquals(12, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESPECIAL, fourthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", fourthInstruction.owner);
		assertEquals("<init>", fourthInstruction.name);
		assertEquals("(Ljava/lang/String;)V", fourthInstruction.desc);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ASTORE, fifthInstruction.getOpcode());
		assertEquals(2, fifthInstruction.var);

		TypeInsnNode sixthInstruction = (TypeInsnNode) instructions.get(5);

		assertEquals(Opcodes.NEW, sixthInstruction.getOpcode());
		assertEquals("java/lang/Thread", sixthInstruction.desc);

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DUP, seventhInstruction.getOpcode());

		VarInsnNode eighthInstruction = (VarInsnNode) instructions.get(7);

		assertEquals(Opcodes.ALOAD, eighthInstruction.getOpcode());
		assertEquals(2, eighthInstruction.var);

		MethodInsnNode ninthInstruction = (MethodInsnNode) instructions.get(8);

		assertEquals(Opcodes.INVOKESPECIAL, ninthInstruction.getOpcode());
		assertEquals("java/lang/Thread", ninthInstruction.owner);
		assertEquals("<init>", ninthInstruction.name);
		assertEquals("(Ljava/lang/Runnable;)V", ninthInstruction.desc);

		MethodInsnNode tenthInstruction = (MethodInsnNode) instructions.get(9);

		assertEquals(Opcodes.INVOKEVIRTUAL, tenthInstruction.getOpcode());
		assertEquals("java/lang/Thread", tenthInstruction.owner);
		assertEquals("start", tenthInstruction.name);
		assertEquals("()V", tenthInstruction.desc);

		VarInsnNode eleventhInstruction = (VarInsnNode) instructions.get(10);

		assertEquals(Opcodes.ALOAD, eleventhInstruction.getOpcode());
		assertEquals(2, eleventhInstruction.var);

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.ARETURN, twelfthInstruction.getOpcode());

		this.testGeneratedClass(result);
	}

	private void testGeneratedClass(CompilationResult result)
	{
		Array<GeneratedClass> generatedClasses = result.getGeneratedClasses();

		assertEquals(1, generatedClasses.length());

		GeneratedClass generatedClass = generatedClasses.get(0);

		ClassReader reader = new ClassReader(generatedClass.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List interfaces = classNode.interfaces;

		assertEquals("myPackage/MyClass$method", generatedClass.getClassName());
		assertEquals(2, interfaces.size());
		assertEquals("java/lang/Runnable", interfaces.get(0));
		assertEquals("java/lang/Comparable", interfaces.get(1));

		List fields = classNode.fields;

		assertEquals(2, fields.size());

		FieldNode firstField = (FieldNode) fields.get(0);

		assertEquals("Ljava/lang/Comparable;", firstField.desc);
		assertEquals("0", firstField.name);

		FieldNode secondField = (FieldNode) fields.get(1);

		assertEquals("Ljava/lang/String;", secondField.desc);
		assertEquals("1", secondField.name);

		List methods = classNode.methods;

		this.testThreadMethod((MethodNode) methods.get(0));
		this.testRunMethod((MethodNode) methods.get(1));
		this.testInterfaceMethod((MethodNode) methods.get(2));
		this.testConstructor((MethodNode) methods.get(3));
	}

	private void testThreadMethod(MethodNode method)
	{
		InsnList instructions = method.instructions;

		assertEquals("(Ljava/lang/String;)Ljava/lang/Comparable;", method.desc);
		assertEquals("method", method.name);
		assertEquals(2, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ARETURN, secondInstruction.getOpcode());
	}

	private void testRunMethod(MethodNode method)
	{
		InsnList instructions = method.instructions;

		assertEquals("run", method.name);
		assertEquals(13, instructions.size());
		assertEquals(1, method.maxLocals);
		assertEquals(3, method.maxStack);

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		FieldInsnNode fourthInstruction = (FieldInsnNode) instructions.get(3);

		assertEquals(Opcodes.GETFIELD, fourthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", fourthInstruction.owner);
		assertEquals("1", fourthInstruction.name);
		assertEquals("Ljava/lang/String;", fourthInstruction.desc);

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEVIRTUAL, fifthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", fifthInstruction.owner);
		assertEquals("method", fifthInstruction.name);
		assertEquals(
			"(Ljava/lang/String;)Ljava/lang/Comparable;",
			fifthInstruction.desc);

		FieldInsnNode sixthInstruction = (FieldInsnNode) instructions.get(5);

		assertEquals(Opcodes.PUTFIELD, sixthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", sixthInstruction.owner);
		assertEquals("0", sixthInstruction.name);
		assertEquals("Ljava/lang/Comparable;", sixthInstruction.desc);

		VarInsnNode seventhInstruction = (VarInsnNode) instructions.get(6);

		assertEquals(Opcodes.ALOAD, seventhInstruction.getOpcode());
		assertEquals(0, seventhInstruction.var);

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.MONITORENTER, eighthInstruction.getOpcode());

		VarInsnNode ninthInstruction = (VarInsnNode) instructions.get(8);

		assertEquals(Opcodes.ALOAD, ninthInstruction.getOpcode());
		assertEquals(0, ninthInstruction.var);

		MethodInsnNode tenthInstruction = (MethodInsnNode) instructions.get(9);

		assertEquals(Opcodes.INVOKEVIRTUAL, tenthInstruction.getOpcode());
		assertEquals("java/lang/Object", tenthInstruction.owner);
		assertEquals("notifyAll", tenthInstruction.name);
		assertEquals("()V", tenthInstruction.desc);

		VarInsnNode eleventhInstruction = (VarInsnNode) instructions.get(10);

		assertEquals(Opcodes.ALOAD, eleventhInstruction.getOpcode());
		assertEquals(0, eleventhInstruction.var);

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.MONITOREXIT, twelfthInstruction.getOpcode());

		InsnNode thirteenthInstruction = (InsnNode) instructions.get(12);

		assertEquals(Opcodes.RETURN, thirteenthInstruction.getOpcode());
	}

	private void testInterfaceMethod(MethodNode methodNode)
	{
		InsnList instructions = methodNode.instructions;

		assertEquals(16, instructions.size());
		assertEquals("compareTo", methodNode.name);
		assertEquals("(Ljava/lang/Object;)I", methodNode.desc);
		assertEquals(2, methodNode.maxLocals);
		assertEquals(2, methodNode.maxStack);

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		FieldInsnNode secondInstruction = (FieldInsnNode) instructions.get(1);

		assertEquals(Opcodes.GETFIELD, secondInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", secondInstruction.owner);
		assertEquals("0", secondInstruction.name);
		assertEquals("Ljava/lang/Comparable;", secondInstruction.desc);

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label label = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IFNONNULL, thirdInstruction.getOpcode());

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ALOAD, fourthInstruction.getOpcode());
		assertEquals(0, fourthInstruction.var);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.MONITORENTER, fifthInstruction.getOpcode());

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.ALOAD, sixthInstruction.getOpcode());
		assertEquals(0, sixthInstruction.var);

		MethodInsnNode seventhInstruction = (MethodInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKEVIRTUAL, seventhInstruction.getOpcode());
		assertEquals("java/lang/Object", seventhInstruction.owner);
		assertEquals("wait", seventhInstruction.name);
		assertEquals("()V", seventhInstruction.desc);

		VarInsnNode eighthInstruction = (VarInsnNode) instructions.get(7);

		assertEquals(Opcodes.ALOAD, eighthInstruction.getOpcode());
		assertEquals(0, eighthInstruction.var);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.MONITOREXIT, ninthInstruction.getOpcode());

		LabelNode tenthInstruction = (LabelNode) instructions.get(9);

		assertEquals(label, tenthInstruction.getLabel());

		assertTrue(instructions.get(10) instanceof FrameNode);

		VarInsnNode twelfthInstruction = (VarInsnNode) instructions.get(11);

		assertEquals(Opcodes.ALOAD, twelfthInstruction.getOpcode());
		assertEquals(0, twelfthInstruction.var);

		FieldInsnNode thirteenthInstruction = (FieldInsnNode) instructions.get(12);

		assertEquals(Opcodes.GETFIELD, thirteenthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", thirteenthInstruction.owner);
		assertEquals("0", thirteenthInstruction.name);
		assertEquals("Ljava/lang/Comparable;", thirteenthInstruction.desc);

		VarInsnNode fourteenthInstruction = (VarInsnNode) instructions.get(13);

		assertEquals(Opcodes.ALOAD, fourteenthInstruction.getOpcode());
		assertEquals(1, fourteenthInstruction.var);

		MethodInsnNode fifteenthInstruction = (MethodInsnNode) instructions.get(14);

		assertEquals(Opcodes.INVOKEINTERFACE, fifteenthInstruction.getOpcode());
		assertEquals("java/lang/Comparable", fifteenthInstruction.owner);
		assertEquals("compareTo", fifteenthInstruction.name);
		assertEquals("(Ljava/lang/Object;)I", fifteenthInstruction.desc);

		InsnNode sixteenthInstruction = (InsnNode) instructions.get(15);

		assertEquals(Opcodes.IRETURN, sixteenthInstruction.getOpcode());
	}

	private void testConstructor(MethodNode constructor)
	{
		assertEquals("(Ljava/lang/String;)V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);
		assertEquals(2, constructor.maxLocals);
		assertEquals(2, constructor.maxStack);

		InsnList instructions = constructor.instructions;

		assertEquals(6, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESPECIAL, secondInstruction.getOpcode());
		assertEquals("java/lang/Object", secondInstruction.owner);
		assertEquals("<init>", secondInstruction.name);
		assertEquals("()V", secondInstruction.desc);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ALOAD, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		FieldInsnNode fifthInstruction = (FieldInsnNode) instructions.get(4);

		assertEquals(Opcodes.PUTFIELD, fifthInstruction.getOpcode());
		assertEquals("Ljava/lang/String;", fifthInstruction.desc);
		assertEquals("1", fifthInstruction.name);

		assertEquals(Opcodes.RETURN, instructions.get(5).getOpcode());
	}
}
