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
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import java.util.List;

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

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		InsnList instructions = firstMethod.instructions;

		assertEquals(14, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ISTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);

		Label continueLabel = thirdInstruction.getLabel();

		assertTrue(instructions.get(3) instanceof  FrameNode);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ILOAD, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);

		LdcInsnNode sixthInstruction = (LdcInsnNode) instructions.get(5);

		assertEquals(Opcodes.LDC, sixthInstruction.getOpcode());
		assertEquals(4, sixthInstruction.cst);

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);
		Label exitLabel = seventhInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ICMPGE, seventhInstruction.getOpcode());

		VarInsnNode eighthInstruction = (VarInsnNode) instructions.get(7);

		assertEquals(Opcodes.ILOAD, eighthInstruction.getOpcode());
		assertEquals(1, eighthInstruction.var);

		VarInsnNode ninthInstruction = (VarInsnNode) instructions.get(8);

		assertEquals(Opcodes.ISTORE, ninthInstruction.getOpcode());
		assertEquals(3, ninthInstruction.var);

		IincInsnNode tenthInstruction = (IincInsnNode) instructions.get(9);

		assertEquals(Opcodes.IINC, tenthInstruction.getOpcode());
		assertEquals(1, tenthInstruction.var);
		assertEquals(1, tenthInstruction.incr);

		JumpInsnNode eleventhInstruction = (JumpInsnNode) instructions.get(10);

		assertEquals(Opcodes.GOTO, eleventhInstruction.getOpcode());
		assertEquals(continueLabel, eleventhInstruction.label.getLabel());

		LabelNode twelfthInstruction = (LabelNode) instructions.get(11);

		assertEquals(exitLabel, twelfthInstruction.getLabel());

		assertTrue(instructions.get(12) instanceof FrameNode);
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
	public void testMissingLoopInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"for (;;) {}\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:5 Missing loop initialiser.\n" +
				"line 5:6 Missing loop exit condition.\n" +
				"line 5:7 Missing loop incrementer.\n",
			result.getErrors());
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
	public void testMissingIncrementer()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"for (Num i = 0; i < 4;) {}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 5:22 Missing loop incrementer.\n", result.getErrors());
	}

	@Test
	public void testMissingIncrementerInMain()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"String thingy = \"thingy\";\n" +
					"for (Num i = 0; i < 4;) {}\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 6:22 Missing loop incrementer.\n", result.getErrors());
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
}
