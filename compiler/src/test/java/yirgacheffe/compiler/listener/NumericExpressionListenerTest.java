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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NumericExpressionListenerTest
{
	@Test
	public void testMultiplicationDivisionAndRemainder()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return 5.0 % 4.0 / 3.0 * 2.0;\n" +
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
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(8, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(5.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(4.0, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DREM, thirdInstruction.getOpcode());

		LdcInsnNode fourthInstruction = (LdcInsnNode) instructions.get(3);

		assertEquals(Opcodes.LDC, fourthInstruction.getOpcode());
		assertEquals(3.0, fourthInstruction.cst);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DDIV, fifthInstruction.getOpcode());

		LdcInsnNode sixthInstruction = (LdcInsnNode) instructions.get(5);

		assertEquals(Opcodes.LDC, sixthInstruction.getOpcode());
		assertEquals(2.0, sixthInstruction.cst);

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DMUL, seventhInstruction.getOpcode());
	}

	@Test
	public void testAdditionAndSubtraction()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return 5.0 - 4.0 + 3.0;\n" +
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
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(6, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(5.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(4.0, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DSUB, thirdInstruction.getOpcode());

		LdcInsnNode fourthInstruction = (LdcInsnNode) instructions.get(3);

		assertEquals(Opcodes.LDC, fourthInstruction.getOpcode());
		assertEquals(3.0, fourthInstruction.cst);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DADD, fifthInstruction.getOpcode());
	}

	@Test
	public void testNegation()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return -(5.0 + 4.0);\n" +
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
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(5, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(5.0, firstInstruction.cst);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals(4.0, secondInstruction.cst);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DADD, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DNEG, fourthInstruction.getOpcode());
	}

	@Test
	public void testNumericOperationsOnStrings()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public String method()" +
				"{\n" +
					"return \"6\" % \"5\" / \"4\" * \"3\" + \"2\" - \"1\";\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 4:31 Cannot subtract java.lang.String and java.lang.String.\n" +
			//"line 4:37 Cannot add java.lang.String and java.lang.String.\n" +
			"line 4:13 Cannot multiply java.lang.String and java.lang.String.\n" +
			"line 4:19 Cannot divide java.lang.String and java.lang.String.\n" +
			"line 4:25 Cannot find remainder of java.lang.String and java.lang.String.\n",
			result.getErrors());
	}

	@Test
	public void testAnd()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"if (5.0 && 4.0)\n" +
					"{\n" +
						"return 0.0;\n" +
					"}\n" +
					"return 1.0;\n" +
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
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(5.0, firstInstruction.cst);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Falsyfier", secondInstruction.owner);
		assertEquals("isTruthy", secondInstruction.name);
		assertEquals("(D)Z", secondInstruction.desc);

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label leftLabel = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, thirdInstruction.getOpcode());

		LdcInsnNode fourthInstruction = (LdcInsnNode) instructions.get(3);

		assertEquals(Opcodes.LDC, fourthInstruction.getOpcode());
		assertEquals(4.0, fourthInstruction.cst);

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKESTATIC, fifthInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Falsyfier", fifthInstruction.owner);
		assertEquals("isTruthy", fifthInstruction.name);
		assertEquals("(D)Z", fifthInstruction.desc);

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);
		Label rightLabel = sixthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DCONST_0, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.DRETURN, eighthInstruction.getOpcode());

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(leftLabel, ninthInstruction.getLabel());
		assertEquals(rightLabel, ninthInstruction.getLabel());

		assertTrue(instructions.get(9) instanceof FrameNode);

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.DCONST_1, eleventhInstruction.getOpcode());

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.DRETURN, twelfthInstruction.getOpcode());
	}

	@Test
	public void testOr()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"if (true || false)\n" +
					"{\n" +
						"return 0.0;\n" +
					"}\n" +
					"return 1.0;\n" +
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
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(11, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);
		Label leftLabel = secondInstruction.label.getLabel();

		assertEquals(Opcodes.IFNE, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.ICONST_0, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label rightLabel = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, fourthInstruction.getOpcode());

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);

		assertEquals(leftLabel, fifthInstruction.getLabel());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.DCONST_0, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DRETURN, seventhInstruction.getOpcode());

		LabelNode eighthInstruction = (LabelNode) instructions.get(7);

		assertEquals(rightLabel, eighthInstruction.getLabel());

		assertTrue(instructions.get(8) instanceof FrameNode);

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.DCONST_1, tenthInstruction.getOpcode());

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.DRETURN, eleventhInstruction.getOpcode());
	}

	@Test
	public void testAndThis()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"if (this && \"thingy\")\n" +
					"{\n" +
						"Num one = 1;\n" +
					"}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testStringAndDouble() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"if (\"thingy\" && 0.0)\n" +
					"{\n" +
					"	Num one = 1;\n" +
					"}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testPostincrement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method(Num i)" +
				"{\n" +
					"return i++;\n" +
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
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(6, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP2, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DADD, fourthInstruction.getOpcode());

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DSTORE, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);
	}

	@Test
	public void testPreincrement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method(Num i)" +
				"{\n" +
					"return ++i;\n" +
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
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(6, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DADD, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DUP2, fourthInstruction.getOpcode());

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DSTORE, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);
	}

	@Test
	public void testPostdecrement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method(Num i)" +
				"{\n" +
					"return i--;\n" +
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
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(6, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP2, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DNEG, fourthInstruction.getOpcode());

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DSTORE, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);
	}

	@Test
	public void testPredecrement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method(Num i)" +
				"{\n" +
					"return --i;\n" +
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
		MethodNode method = (MethodNode) methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(6, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DNEG, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DUP2, fourthInstruction.getOpcode());

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DSTORE, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);
	}
}
