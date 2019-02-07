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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.GeneratedClass;
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
				"parallel public Comparable<String> method()" +
				"{\n" +
					"return \"thingy\";\n" +
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

		assertEquals("()Ljava/lang/Comparable;", firstMethod.desc);
		assertEquals(2, firstMethod.maxLocals);
		assertEquals(3, firstMethod.maxStack);
		assertEquals(11, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESPECIAL, thirdInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", thirdInstruction.owner);
		assertEquals("<init>", thirdInstruction.name);
		assertEquals("()V", thirdInstruction.desc);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ASTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		TypeInsnNode fifthInstruction = (TypeInsnNode) instructions.get(4);

		assertEquals(Opcodes.NEW, fifthInstruction.getOpcode());
		assertEquals("java/lang/Thread", fifthInstruction.desc);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.DUP, sixthInstruction.getOpcode());

		VarInsnNode seventhInstructions = (VarInsnNode) instructions.get(6);

		assertEquals(Opcodes.ALOAD, seventhInstructions.getOpcode());
		assertEquals(1, seventhInstructions.var);

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKESPECIAL, eighthInstruction.getOpcode());
		assertEquals("java/lang/Thread", eighthInstruction.owner);
		assertEquals("<init>", eighthInstruction.name);
		assertEquals("(Ljava/lang/Runnable;)V", eighthInstruction.desc);

		MethodInsnNode ninthInstruction = (MethodInsnNode) instructions.get(8);

		assertEquals(Opcodes.INVOKEVIRTUAL, ninthInstruction.getOpcode());
		assertEquals("java/lang/Thread", ninthInstruction.owner);
		assertEquals("start", ninthInstruction.name);
		assertEquals("()V", ninthInstruction.desc);

		VarInsnNode tenthInstruction = (VarInsnNode) instructions.get(9);

		assertEquals(Opcodes.ALOAD, tenthInstruction.getOpcode());
		assertEquals(1, tenthInstruction.var);

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.ARETURN, eleventhInstruction.getOpcode());

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

		assertEquals(1, fields.size());

		FieldNode firstField = (FieldNode) fields.get(0);

		assertEquals("Ljava/lang/Comparable;", firstField.desc);
		assertEquals("0", firstField.name);

		List methods = classNode.methods;

		this.testThreadMethod((MethodNode) methods.get(0));
		this.testRunMethod((MethodNode) methods.get(1));
		this.testInterfaceMethod((MethodNode) methods.get(2));
		this.testConstructor((MethodNode) methods.get(3));
	}

	private void testThreadMethod(MethodNode method)
	{
		InsnList instructions = method.instructions;

		assertEquals("()Ljava/lang/Comparable;", method.desc);
		assertEquals("method", method.name);
		assertEquals(2, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ARETURN, secondInstruction.getOpcode());
	}

	private void testRunMethod(MethodNode method)
	{
		InsnList instructions = method.instructions;

		assertEquals("run", method.name);
		assertEquals(11, instructions.size());
		assertEquals(1, method.maxLocals);
		assertEquals(2, method.maxStack);

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", thirdInstruction.owner);
		assertEquals("method", thirdInstruction.name);
		assertEquals("()Ljava/lang/Comparable;", thirdInstruction.desc);

		FieldInsnNode fourthInstruction = (FieldInsnNode) instructions.get(3);

		assertEquals(Opcodes.PUTFIELD, fourthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", fourthInstruction.owner);
		assertEquals("0", fourthInstruction.name);
		assertEquals("Ljava/lang/Comparable;", fourthInstruction.desc);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ALOAD, fifthInstruction.getOpcode());
		assertEquals(0, fifthInstruction.var);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.MONITORENTER, sixthInstruction.getOpcode());

		VarInsnNode seventhInstruction = (VarInsnNode) instructions.get(6);

		assertEquals(Opcodes.ALOAD, seventhInstruction.getOpcode());
		assertEquals(0, seventhInstruction.var);

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKEVIRTUAL, eighthInstruction.getOpcode());
		assertEquals("java/lang/Object", eighthInstruction.owner);
		assertEquals("notifyAll", eighthInstruction.name);
		assertEquals("()V", eighthInstruction.desc);

		VarInsnNode ninthInstruction = (VarInsnNode) instructions.get(8);

		assertEquals(Opcodes.ALOAD, ninthInstruction.getOpcode());
		assertEquals(0, ninthInstruction.var);

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.MONITOREXIT, tenthInstruction.getOpcode());

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.RETURN, eleventhInstruction.getOpcode());
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
		assertEquals("()V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);
		assertEquals(1, constructor.maxLocals);
		assertEquals(1, constructor.maxStack);

		InsnList instructions = constructor.instructions;

		assertEquals(3, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESPECIAL, secondInstruction.getOpcode());
		assertEquals("java/lang/Object", secondInstruction.owner);
		assertEquals("<init>", secondInstruction.name);
		assertEquals("()V", secondInstruction.desc);

		assertEquals(Opcodes.RETURN, instructions.get(2).getOpcode());
	}
}
