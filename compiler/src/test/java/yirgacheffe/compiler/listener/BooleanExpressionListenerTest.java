package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BooleanExpressionListenerTest
{
	@Test
	public void testEquals()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 == 2.0;\n" +
				"}\n" +
				"public MyClass() {}" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFNE, fourthInstruction.getOpcode());
	}

	@Test
	public void testNotEquals()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 != 2.0;\n" +
				"}\n" +
				"public MyClass() {}" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFEQ, fourthInstruction.getOpcode());
	}

	@Test
	public void testLessThan()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 < 2.0;\n" +
				"}\n" +
				"public MyClass() {}" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFGE, fourthInstruction.getOpcode());
	}

	@Test
	public void testGreaterThan()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 > 2.0;\n" +
				"}\n" +
				"public MyClass() {}" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFLE, fourthInstruction.getOpcode());
	}

	@Test
	public void testLessThanOrEqual()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 <= 2.0;\n" +
				"}\n" +
				"public MyClass() {}" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFGT, fourthInstruction.getOpcode());
	}

	@Test
	public void testGreaterThanOrEqual()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method()" +
				"{\n" +
					"return 1.0 >= 2.0;\n" +
				"}\n" +
				"public MyClass() {}" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);

		assertEquals(Opcodes.IFLT, fourthInstruction.getOpcode());
	}

	@Test
	public void testEqualStringCondition()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"if (\"thingy\" == \"sumpt\") {}\n" +
				"}\n" +
				"public MyClass() {}" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(7, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("sumpt", secondInstruction.cst);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/String", thirdInstruction.owner);
		assertEquals("equals", thirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)Z", thirdInstruction.desc);

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label label = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, fourthInstruction.getOpcode());

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);

		assertEquals(label, fifthInstruction.getLabel());
	}

	@Test
	public void testNotEqualStringCondition()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"if (\"thingy\" != \"sumpt\") {}\n" +
				"}\n" +
				"public MyClass() {}" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(7, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("sumpt", secondInstruction.cst);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/String", thirdInstruction.owner);
		assertEquals("equals", thirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)Z", thirdInstruction.desc);

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label label = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNE, fourthInstruction.getOpcode());

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);

		assertEquals(label, fifthInstruction.getLabel());
	}

	@Test
	public void testMultipleDoubleInequalities()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Bool method(Num x)" +
				"{\n" +
					"return 1.0 < x > 2.0;\n" +
				"}\n" +
				"public MyClass() {}" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(23, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.DCONST_1, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DLOAD, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DUP2_X2, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCMPG, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);
		Label firstLabel = fifthInstruction.label.getLabel();

		assertEquals(Opcodes.IFGE, fifthInstruction.getOpcode());

		LdcInsnNode sixthInstruction = (LdcInsnNode) instructions.get(5);

		assertEquals(Opcodes.LDC, sixthInstruction.getOpcode());
		assertEquals(2.0, sixthInstruction.cst);

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DCMPL, seventhInstruction.getOpcode());

		JumpInsnNode eighthInstruction = (JumpInsnNode) instructions.get(7);
		Label secondLabel = eighthInstruction.label.getLabel();

		assertEquals(Opcodes.IFLE, eighthInstruction.getOpcode());

		JumpInsnNode ninthInstruction = (JumpInsnNode) instructions.get(8);
		Label thirdLabel = ninthInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, ninthInstruction.getOpcode());

		LabelNode tenthInstruction = (LabelNode) instructions.get(9);

		assertEquals(firstLabel, tenthInstruction.getLabel());

		assertTrue(instructions.get(10) instanceof FrameNode);

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.POP2, twelfthInstruction.getOpcode());

		JumpInsnNode thirteenthInstruction = (JumpInsnNode) instructions.get(12);
		Label fourthLabel = thirteenthInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, thirteenthInstruction.getOpcode());

		LabelNode fourteenthInstruction = (LabelNode) instructions.get(13);

		assertEquals(thirdLabel, fourteenthInstruction.getLabel());

		assertTrue(instructions.get(14) instanceof FrameNode);

		InsnNode sixteenthInstruction = (InsnNode) instructions.get(15);

		assertEquals(Opcodes.ICONST_1, sixteenthInstruction.getOpcode());

		JumpInsnNode seventeenthInstruction = (JumpInsnNode) instructions.get(16);
		Label fifthLabel = seventeenthInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, seventeenthInstruction.getOpcode());

		LabelNode eighteenthInstruction = (LabelNode) instructions.get(17);

		assertEquals(secondLabel, eighteenthInstruction.getLabel());

		assertTrue(instructions.get(18) instanceof FrameNode);

		InsnNode nineteenthInstruction = (InsnNode) instructions.get(19);

		assertEquals(Opcodes.ICONST_0, nineteenthInstruction.getOpcode());
	}
}
