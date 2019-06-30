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
				"public MyClass() {}\n" +
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
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("myPackage/MyClass.yg", source);

		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);

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

		assertEquals(5, fields.size());

		FieldNode exceptionField = (FieldNode) fields.get(0);

		assertEquals("Ljava/lang/Throwable;", exceptionField.desc);
		assertEquals("0exception", exceptionField.name);

		FieldNode ranField = (FieldNode) fields.get(1);

		assertEquals("Z", ranField.desc);
		assertEquals("0ran", ranField.name);

		FieldNode returnField = (FieldNode) fields.get(2);

		assertEquals("Ljava/lang/Comparable;", returnField.desc);
		assertEquals("0return", returnField.name);

		FieldNode firstField = (FieldNode) fields.get(3);

		assertEquals("LmyPackage/MyClass;", firstField.desc);
		assertEquals("0", firstField.name);

		FieldNode secondField = (FieldNode) fields.get(4);

		assertEquals("Ljava/lang/String;", secondField.desc);
		assertEquals("1", secondField.name);

		this.testThreadMethod(classNode.methods.get(0));
		this.testRunMethod(classNode.methods.get(1));
		this.testInterfaceMethod(classNode.methods.get(2));
		this.testConstructor(classNode.methods.get(3));
	}

	private void testThreadMethod(MethodNode method)
	{
		InsnList instructions = method.instructions;

		assertEquals(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, method.access);
		assertEquals("method", method.name);
		assertEquals(1, method.maxStack);
		assertEquals(2, method.maxLocals);
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
		assertEquals(27, instructions.size());
		assertEquals(1, method.maxLocals);
		assertEquals(3, method.maxStack);

		assertTrue(instructions.get(0) instanceof LabelNode);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ALOAD, secondInstruction.getOpcode());
		assertEquals(0, secondInstruction.var);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DUP, thirdInstruction.getOpcode());

		FieldInsnNode fourthInstruction = (FieldInsnNode) instructions.get(3);

		assertEquals(Opcodes.GETFIELD, fourthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", fourthInstruction.owner);
		assertEquals("0", fourthInstruction.name);
		assertEquals("LmyPackage/MyClass;", fourthInstruction.desc);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ALOAD, fifthInstruction.getOpcode());
		assertEquals(0, fifthInstruction.var);

		FieldInsnNode sixthInstruction = (FieldInsnNode) instructions.get(5);

		assertEquals(Opcodes.GETFIELD, sixthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", sixthInstruction.owner);
		assertEquals("1", sixthInstruction.name);
		assertEquals("Ljava/lang/String;", sixthInstruction.desc);

		MethodInsnNode seventhInstruction = (MethodInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKESTATIC, seventhInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", seventhInstruction.owner);
		assertEquals("method", seventhInstruction.name);
		assertEquals(
			"(LmyPackage/MyClass;Ljava/lang/String;)Ljava/lang/Comparable;",
			seventhInstruction.desc);

		FieldInsnNode eighthInstruction = (FieldInsnNode) instructions.get(7);

		assertEquals(Opcodes.PUTFIELD, eighthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", eighthInstruction.owner);
		assertEquals("0return", eighthInstruction.name);
		assertEquals("Ljava/lang/Comparable;", eighthInstruction.desc);

		assertTrue(instructions.get(8) instanceof LabelNode);

		JumpInsnNode tenthInstruction = (JumpInsnNode) instructions.get(9);

		assertEquals(Opcodes.GOTO, tenthInstruction.getOpcode());

		Label goToLabel = tenthInstruction.label.getLabel();

		assertTrue(instructions.get(10) instanceof LabelNode);
		assertTrue(instructions.get(11) instanceof FrameNode);

		VarInsnNode thirteenthInstruction = (VarInsnNode) instructions.get(12);

		assertEquals(Opcodes.ALOAD, thirteenthInstruction.getOpcode());
		assertEquals(0, thirteenthInstruction.var);

		InsnNode fourteenthInstruction = (InsnNode) instructions.get(13);

		assertEquals(Opcodes.SWAP, fourteenthInstruction.getOpcode());

		FieldInsnNode fifteenthInstruction = (FieldInsnNode) instructions.get(14);

		assertEquals(Opcodes.PUTFIELD, fifteenthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", fifteenthInstruction.owner);
		assertEquals("0exception", fifteenthInstruction.name);
		assertEquals("Ljava/lang/Throwable;", fifteenthInstruction.desc);

		LabelNode sixteenthInstruction = (LabelNode) instructions.get(15);

		assertEquals(goToLabel, sixteenthInstruction.getLabel());

		assertTrue(instructions.get(16) instanceof FrameNode);

		VarInsnNode varInstruction = (VarInsnNode) instructions.get(17);

		assertEquals(Opcodes.ALOAD, varInstruction.getOpcode());
		assertEquals(0, varInstruction.var);

		InsnNode trueInstruction = (InsnNode) instructions.get(18);

		assertEquals(Opcodes.ICONST_1, trueInstruction.getOpcode());

		FieldInsnNode fieldInsnNode = (FieldInsnNode) instructions.get(19);

		assertEquals(Opcodes.PUTFIELD, fieldInsnNode.getOpcode());
		assertEquals("myPackage/MyClass$method", fieldInsnNode.owner);
		assertEquals("0ran", fieldInsnNode.name);
		assertEquals("Z", fieldInsnNode.desc);

		VarInsnNode eighteenthInstruction = (VarInsnNode) instructions.get(20);

		assertEquals(Opcodes.ALOAD, eighteenthInstruction.getOpcode());
		assertEquals(0, eighteenthInstruction.var);

		InsnNode nineteenthInstruction = (InsnNode) instructions.get(21);

		assertEquals(Opcodes.MONITORENTER, nineteenthInstruction.getOpcode());

		VarInsnNode twentiethInstruction = (VarInsnNode) instructions.get(22);

		assertEquals(Opcodes.ALOAD, twentiethInstruction.getOpcode());
		assertEquals(0, twentiethInstruction.var);

		MethodInsnNode twentyFirstInstruction = (MethodInsnNode) instructions.get(23);

		assertEquals(Opcodes.INVOKEVIRTUAL, twentyFirstInstruction.getOpcode());
		assertEquals("java/lang/Object", twentyFirstInstruction.owner);
		assertEquals("notifyAll", twentyFirstInstruction.name);
		assertEquals("()V", twentyFirstInstruction.desc);

		VarInsnNode twentySecondInstruction = (VarInsnNode) instructions.get(24);

		assertEquals(Opcodes.ALOAD, twentySecondInstruction.getOpcode());
		assertEquals(0, twentySecondInstruction.var);

		InsnNode twentyThirdInstruction = (InsnNode) instructions.get(25);

		assertEquals(Opcodes.MONITOREXIT, twentyThirdInstruction.getOpcode());

		InsnNode twentyFourthInstruction = (InsnNode) instructions.get(26);

		assertEquals(Opcodes.RETURN, twentyFourthInstruction.getOpcode());
	}

	private void testInterfaceMethod(MethodNode methodNode)
	{
		InsnList instructions = methodNode.instructions;

		assertEquals(24, instructions.size());
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
		assertEquals("0ran", secondInstruction.name);
		assertEquals("Z", secondInstruction.desc);

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label label = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IFNE, thirdInstruction.getOpcode());

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
		assertEquals("0exception", thirteenthInstruction.name);
		assertEquals("Ljava/lang/Throwable;", thirteenthInstruction.desc);

		JumpInsnNode fourteenthInstruction = (JumpInsnNode) instructions.get(13);
		Label secondExceptionLabel = fourteenthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNULL, fourteenthInstruction.getOpcode());

		VarInsnNode fifteenthInstruction = (VarInsnNode) instructions.get(14);

		assertEquals(Opcodes.ALOAD, fifteenthInstruction.getOpcode());
		assertEquals(0, fifteenthInstruction.var);

		FieldInsnNode sixteenthInstruction = (FieldInsnNode) instructions.get(15);

		assertEquals(Opcodes.GETFIELD, sixteenthInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", sixteenthInstruction.owner);
		assertEquals("0exception", sixteenthInstruction.name);
		assertEquals("Ljava/lang/Throwable;", sixteenthInstruction.desc);

		InsnNode seventeenthInstruction = (InsnNode) instructions.get(16);

		assertEquals(Opcodes.ATHROW, seventeenthInstruction.getOpcode());

		LabelNode eighteenthInstruction = (LabelNode) instructions.get(17);

		assertEquals(secondExceptionLabel, eighteenthInstruction.getLabel());

		assertTrue(instructions.get(18) instanceof FrameNode);

		VarInsnNode twentiethInstruction = (VarInsnNode) instructions.get(19);

		assertEquals(Opcodes.ALOAD, twentiethInstruction.getOpcode());
		assertEquals(0, twentiethInstruction.var);

		FieldInsnNode twentyFirstInstruction = (FieldInsnNode) instructions.get(20);

		assertEquals(Opcodes.GETFIELD, twentyFirstInstruction.getOpcode());
		assertEquals("myPackage/MyClass$method", twentyFirstInstruction.owner);
		assertEquals("0return", twentyFirstInstruction.name);
		assertEquals("Ljava/lang/Comparable;", twentyFirstInstruction.desc);

		VarInsnNode twentySecondInstruction = (VarInsnNode) instructions.get(21);

		assertEquals(Opcodes.ALOAD, twentySecondInstruction.getOpcode());
		assertEquals(1, twentySecondInstruction.var);

		MethodInsnNode twentyThirdInstruction = (MethodInsnNode) instructions.get(22);

		assertEquals(Opcodes.INVOKEINTERFACE, twentyThirdInstruction.getOpcode());
		assertEquals("java/lang/Comparable", twentyThirdInstruction.owner);
		assertEquals("compareTo", twentyThirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)I", twentyThirdInstruction.desc);

		InsnNode twentyFourthInstruction = (InsnNode) instructions.get(23);

		assertEquals(Opcodes.IRETURN, twentyFourthInstruction.getOpcode());
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
