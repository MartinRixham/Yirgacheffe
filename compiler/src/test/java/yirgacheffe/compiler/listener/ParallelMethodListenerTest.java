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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParallelMethodListenerTest
{
	@Test
	public void testParallelMethodNonInterfaceReturnType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"parallel public String method()" +
				"{\n" +
					"return \"thingy\";\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);

		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Parallel method must have interface return type.\n",
			result.getErrors());
	}

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
		assertEquals(4, firstMethod.maxStack);
		assertEquals(13, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ALOAD, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKESPECIAL, fifthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", fifthInstruction.owner);
		assertEquals("<init>", fifthInstruction.name);
		assertEquals("(LmyPackage/MyClass;Ljava/lang/String;)V", fifthInstruction.desc);

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.ASTORE, sixthInstruction.getOpcode());
		assertEquals(2, sixthInstruction.var);

		TypeInsnNode seventhInstruction = (TypeInsnNode) instructions.get(6);

		assertEquals(Opcodes.NEW, seventhInstruction.getOpcode());
		assertEquals("java/lang/Thread", seventhInstruction.desc);

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.DUP, eighthInstruction.getOpcode());

		VarInsnNode ninthInstruction = (VarInsnNode) instructions.get(8);

		assertEquals(Opcodes.ALOAD, ninthInstruction.getOpcode());
		assertEquals(2, ninthInstruction.var);

		MethodInsnNode tenthInstruction = (MethodInsnNode) instructions.get(9);

		assertEquals(Opcodes.INVOKESPECIAL, tenthInstruction.getOpcode());
		assertEquals("java/lang/Thread", tenthInstruction.owner);
		assertEquals("<init>", tenthInstruction.name);
		assertEquals("(Ljava/lang/Runnable;)V", tenthInstruction.desc);

		MethodInsnNode eleventhInstruction = (MethodInsnNode) instructions.get(10);

		assertEquals(Opcodes.INVOKEVIRTUAL, eleventhInstruction.getOpcode());
		assertEquals("java/lang/Thread", eleventhInstruction.owner);
		assertEquals("start", eleventhInstruction.name);
		assertEquals("()V", eleventhInstruction.desc);

		VarInsnNode twelfthInstruction = (VarInsnNode) instructions.get(11);

		assertEquals(Opcodes.ALOAD, twelfthInstruction.getOpcode());
		assertEquals(2, twelfthInstruction.var);

		InsnNode thirteenthInstruction = (InsnNode) instructions.get(12);

		assertEquals(Opcodes.ARETURN, thirteenthInstruction.getOpcode());

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

		assertEquals(3, fields.size());

		FieldNode returnField = (FieldNode) fields.get(0);

		assertEquals("Ljava/lang/Comparable;", returnField.desc);
		assertEquals("0return", returnField.name);

		FieldNode firstField = (FieldNode) fields.get(1);

		assertEquals("LmyPackage/MyClass;", firstField.desc);
		assertEquals("0", firstField.name);

		FieldNode secondField = (FieldNode) fields.get(2);

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

		assertEquals(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, method.access);
		assertEquals("method", method.name);
		assertEquals(2, instructions.size());
		assertEquals(
			"(LmyPackage/MyClass;Ljava/lang/String;)Ljava/lang/Comparable;",
			method.desc);

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
		assertEquals(14, instructions.size());
		assertEquals(1, method.maxLocals);
		assertEquals(3, method.maxStack);

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		FieldInsnNode thirdInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.GETFIELD, thirdInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", thirdInstruction.owner);
		assertEquals("0", thirdInstruction.name);
		assertEquals("LmyPackage/MyClass;", thirdInstruction.desc);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ALOAD, fourthInstruction.getOpcode());
		assertEquals(0, fourthInstruction.var);

		FieldInsnNode fifthInstruction = (FieldInsnNode) instructions.get(4);

		assertEquals(Opcodes.GETFIELD, fifthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", fifthInstruction.owner);
		assertEquals("1", fifthInstruction.name);
		assertEquals("Ljava/lang/String;", fifthInstruction.desc);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESTATIC, sixthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", sixthInstruction.owner);
		assertEquals("method", sixthInstruction.name);
		assertEquals(
			"(LmyPackage/MyClass;Ljava/lang/String;)Ljava/lang/Comparable;",
			sixthInstruction.desc);

		FieldInsnNode seventhInstruction = (FieldInsnNode) instructions.get(6);

		assertEquals(Opcodes.PUTFIELD, seventhInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", seventhInstruction.owner);
		assertEquals("0return", seventhInstruction.name);
		assertEquals("Ljava/lang/Comparable;", seventhInstruction.desc);

		VarInsnNode eighthInstruction = (VarInsnNode) instructions.get(7);

		assertEquals(Opcodes.ALOAD, eighthInstruction.getOpcode());
		assertEquals(0, eighthInstruction.var);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.MONITORENTER, ninthInstruction.getOpcode());

		VarInsnNode tenthInstruction = (VarInsnNode) instructions.get(9);

		assertEquals(Opcodes.ALOAD, tenthInstruction.getOpcode());
		assertEquals(0, tenthInstruction.var);

		MethodInsnNode eleventhInstruction = (MethodInsnNode) instructions.get(10);

		assertEquals(Opcodes.INVOKEVIRTUAL, eleventhInstruction.getOpcode());
		assertEquals("java/lang/Object", eleventhInstruction.owner);
		assertEquals("notifyAll", eleventhInstruction.name);
		assertEquals("()V", eleventhInstruction.desc);

		VarInsnNode twelfthInstruction = (VarInsnNode) instructions.get(11);

		assertEquals(Opcodes.ALOAD, twelfthInstruction.getOpcode());
		assertEquals(0, twelfthInstruction.var);

		InsnNode thirteenthInstruction = (InsnNode) instructions.get(12);

		assertEquals(Opcodes.MONITOREXIT, thirteenthInstruction.getOpcode());

		InsnNode fourteenthInstruction = (InsnNode) instructions.get(13);

		assertEquals(Opcodes.RETURN, fourteenthInstruction.getOpcode());
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
		assertEquals("0return", secondInstruction.name);
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
		assertEquals("0return", thirteenthInstruction.name);
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
		assertEquals("(LmyPackage/MyClass;Ljava/lang/String;)V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);
		assertEquals(3, constructor.maxLocals);
		assertEquals(2, constructor.maxStack);

		InsnList instructions = constructor.instructions;

		assertEquals(9, instructions.size());

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
		assertEquals("LmyPackage/MyClass;", fifthInstruction.desc);
		assertEquals("0", fifthInstruction.name);

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.ALOAD, sixthInstruction.getOpcode());
		assertEquals(0, sixthInstruction.var);

		VarInsnNode seventhInstruction = (VarInsnNode) instructions.get(6);

		assertEquals(Opcodes.ALOAD, seventhInstruction.getOpcode());
		assertEquals(2, seventhInstruction.var);

		FieldInsnNode eighthInstruction = (FieldInsnNode) instructions.get(7);

		assertEquals(Opcodes.PUTFIELD, eighthInstruction.getOpcode());
		assertEquals("Ljava/lang/String;", eighthInstruction.desc);
		assertEquals("1", eighthInstruction.name);

		assertEquals(Opcodes.RETURN, instructions.get(8).getOpcode());
	}
}
