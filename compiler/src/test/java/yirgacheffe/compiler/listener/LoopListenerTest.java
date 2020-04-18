package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoopListenerTest
{
	@Test
	public void testForLoop()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"for (Num i = 0; i < 4; i++)\n" +
					"{\n" +
						"Num index = i;\n" +
					"}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);
		InsnList instructions = firstMethod.instructions;

		assertEquals(16, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ISTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);

		Label continueLabel = thirdInstruction.getLabel();

		assertTrue(instructions.get(3) instanceof FrameNode);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ILOAD, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.I2L, sixthInstruction.getOpcode());

		LdcInsnNode seventhInstruction = (LdcInsnNode) instructions.get(6);

		assertEquals(Opcodes.LDC, seventhInstruction.getOpcode());
		assertEquals(4L, seventhInstruction.cst);

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.LCMP, eighthInstruction.getOpcode());

		JumpInsnNode ninthInstruction = (JumpInsnNode) instructions.get(8);
		Label exitLabel = ninthInstruction.label.getLabel();

		assertEquals(Opcodes.IFGE, ninthInstruction.getOpcode());

		VarInsnNode tenthInstruction = (VarInsnNode) instructions.get(9);

		assertEquals(Opcodes.ILOAD, tenthInstruction.getOpcode());
		assertEquals(1, tenthInstruction.var);

		VarInsnNode eleventhInstruction = (VarInsnNode) instructions.get(10);

		assertEquals(Opcodes.ISTORE, eleventhInstruction.getOpcode());
		assertEquals(3, eleventhInstruction.var);

		IincInsnNode twelfthInstruction = (IincInsnNode) instructions.get(11);

		assertEquals(Opcodes.IINC, twelfthInstruction.getOpcode());
		assertEquals(1, twelfthInstruction.var);
		assertEquals(1, twelfthInstruction.incr);

		JumpInsnNode thirteenthInstruction = (JumpInsnNode) instructions.get(12);

		assertEquals(Opcodes.GOTO, thirteenthInstruction.getOpcode());
		assertEquals(continueLabel, thirteenthInstruction.label.getLabel());

		LabelNode fourteenthInstruction = (LabelNode) instructions.get(13);

		assertEquals(exitLabel, fourteenthInstruction.getLabel());

		assertTrue(instructions.get(14) instanceof FrameNode);
	}

	@Test
	public void testForLoopOnArray()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method(Array<String> array)" +
				"{\n" +
				"for (Num i = 0; i < array.length(); i++)\n" +
				"{\n" +
					"Num index = i;\n" +
				"}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);
		InsnList instructions = firstMethod.instructions;

		assertEquals(17, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ISTORE, secondInstruction.getOpcode());
		assertEquals(2, secondInstruction.var);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);

		Label continueLabel = thirdInstruction.getLabel();

		assertTrue(instructions.get(3) instanceof FrameNode);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ILOAD, fifthInstruction.getOpcode());
		assertEquals(2, fifthInstruction.var);

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.ALOAD, sixthInstruction.getOpcode());
		assertEquals(1, sixthInstruction.var);

		assertTrue(instructions.get(6) instanceof LabelNode);
		assertTrue(instructions.get(7) instanceof LineNumberNode);
	}

	@Test
	public void testForLoopWithoutEquation()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method(Bool bool)" +
				"{\n" +
					"for (Num i = 0; bool; i++)\n" +
					"{\n" +
						"Num index = i;\n" +
					"}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);
		InsnList instructions = firstMethod.instructions;

		assertEquals(13, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ISTORE, secondInstruction.getOpcode());
		assertEquals(2, secondInstruction.var);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);

		Label continueLabel = thirdInstruction.getLabel();

		assertTrue(instructions.get(3) instanceof  FrameNode);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ILOAD, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);

		Label exitLabel = sixthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, sixthInstruction.getOpcode());

		VarInsnNode seventhInstruction = (VarInsnNode) instructions.get(6);

		assertEquals(Opcodes.ILOAD, seventhInstruction.getOpcode());
		assertEquals(2, seventhInstruction.var);

		VarInsnNode eighthInstruction = (VarInsnNode) instructions.get(7);

		assertEquals(Opcodes.ISTORE, eighthInstruction.getOpcode());
		assertEquals(4, eighthInstruction.var);

		IincInsnNode ninthInstruction = (IincInsnNode) instructions.get(8);

		assertEquals(Opcodes.IINC, ninthInstruction.getOpcode());
		assertEquals(2, ninthInstruction.var);
		assertEquals(1, ninthInstruction.incr);

		JumpInsnNode tenthInstruction = (JumpInsnNode) instructions.get(9);

		assertEquals(Opcodes.GOTO, tenthInstruction.getOpcode());
		assertEquals(continueLabel, tenthInstruction.label.getLabel());

		LabelNode eleventhInstruction = (LabelNode) instructions.get(10);

		assertEquals(exitLabel, eleventhInstruction.getLabel());

		assertTrue(instructions.get(11) instanceof FrameNode);
	}

	@Test
	public void testMalformedLoop()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"for ()\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
	}

	@Test
	public void testMissingExitCondition()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"for (Num i = 1;;i++) {}\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:15 Missing loop exit condition.\n",
			result.getErrors());
	}

	@Test
	public void testInfiniteLoop()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"String thingy = \"thingy\";\n" +
					"for (; true;)\n" +
					"{\n" +
						"thingy = \"sumpt\";" +
					"}\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);
		InsnList instructions = firstMethod.instructions;

		assertEquals(10, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ASTORE, secondInstruction.getOpcode());
		assertEquals(2, secondInstruction.var);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);

		Label label = thirdInstruction.getLabel();

		assertTrue(instructions.get(3) instanceof FrameNode);

		LdcInsnNode fifthInstruction = (LdcInsnNode) instructions.get(4);

		assertEquals(Opcodes.LDC, fifthInstruction.getOpcode());
		assertEquals("sumpt", fifthInstruction.cst);

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.ASTORE, sixthInstruction.getOpcode());
		assertEquals(2, sixthInstruction.var);

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);

		assertEquals(Opcodes.GOTO, seventhInstruction.getOpcode());
		assertEquals(label, seventhInstruction.label.getLabel());
	}

	@Test
	public void testLoopAfterParameters()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method(String arg1, String arg2)\n" +
				"{\n" +
					"for (Num i = 0; i < 4; i++) {}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testLoopWithMissingStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"for (Num i = 0; i < 4; i++)\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testAccessIndexAfterLoop()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"for (Num i = 0; i < 4; i++) {}\n" +
					"i++;\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 6:0 Unknown local variable 'i'.\n", result.getErrors());
	}

	@Test
	public void testLoopOverNothing()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"for (Num i = 0; i < args.length(); i++)\n" +
				"}\n" +
			"}\n";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}
}
