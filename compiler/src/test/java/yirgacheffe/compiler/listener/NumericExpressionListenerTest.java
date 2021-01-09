package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

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

		MethodNode method = classNode.methods.get(0);
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

		MethodNode method = classNode.methods.get(0);
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

		MethodNode method = classNode.methods.get(0);
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
			"line 4:13 Cannot multiply java.lang.String and java.lang.String.\n" +
			"line 4:19 Cannot divide java.lang.String and java.lang.String.\n" +
			"line 4:25 Cannot find remainder of java.lang.String and java.lang.String." +
			"\nline 4:31 Cannot subtract java.lang.String and java.lang.String.\n",
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

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(18, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(5.0, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCMPL, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ICONST_1, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.IADD, fifthInstruction.getOpcode());

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);
		Label leftLabel = sixthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, sixthInstruction.getOpcode());

		LdcInsnNode seventhInstruction = (LdcInsnNode) instructions.get(6);

		assertEquals(Opcodes.LDC, seventhInstruction.getOpcode());
		assertEquals(4.0, seventhInstruction.cst);

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.DCONST_1, eighthInstruction.getOpcode());

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.DCMPL, ninthInstruction.getOpcode());

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.ICONST_1, tenthInstruction.getOpcode());

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.IADD, eleventhInstruction.getOpcode());

		JumpInsnNode twelfthInstruction = (JumpInsnNode) instructions.get(11);
		Label rightLabel = twelfthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, twelfthInstruction.getOpcode());

		InsnNode thirteenthInstruction = (InsnNode) instructions.get(12);

		assertEquals(Opcodes.DCONST_0, thirteenthInstruction.getOpcode());

		InsnNode fourteenthInstruction = (InsnNode) instructions.get(13);

		assertEquals(Opcodes.DRETURN, fourteenthInstruction.getOpcode());

		LabelNode fifteenthInstruction = (LabelNode) instructions.get(14);

		assertEquals(leftLabel, fifteenthInstruction.getLabel());
		assertEquals(rightLabel, fifteenthInstruction.getLabel());

		assertTrue(instructions.get(15) instanceof FrameNode);

		InsnNode seventeenthInstruction = (InsnNode) instructions.get(16);

		assertEquals(Opcodes.DCONST_1, seventeenthInstruction.getOpcode());

		InsnNode eighteenthInstruction = (InsnNode) instructions.get(17);

		assertEquals(Opcodes.DRETURN, eighteenthInstruction.getOpcode());
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

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

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

		assertTrue(instructions.get(5) instanceof FrameNode);

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DCONST_0, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.DRETURN, eighthInstruction.getOpcode());

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(rightLabel, ninthInstruction.getLabel());

		assertTrue(instructions.get(9) instanceof FrameNode);

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.DCONST_1, eleventhInstruction.getOpcode());

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.DRETURN, twelfthInstruction.getOpcode());
	}

	@Test
	public void testBooleanVariable()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"Bool tru = true || false;\n" +
					"if (tru)\n" +
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

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(12, instructions.size());

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

		assertTrue(instructions.get(5) instanceof FrameNode);

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DCONST_0, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.DRETURN, eighthInstruction.getOpcode());

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(rightLabel, ninthInstruction.getLabel());

		assertTrue(instructions.get(9) instanceof FrameNode);

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.DCONST_1, eleventhInstruction.getOpcode());

		InsnNode twelfthInstruction = (InsnNode) instructions.get(11);

		assertEquals(Opcodes.DRETURN, twelfthInstruction.getOpcode());
	}

	@Test
	public void testMultipleAnds()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"if (this && \"thingy\" && 1)\n" +
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

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(21, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFNULL, secondInstruction.getOpcode());
		Label firstLabel = secondInstruction.label.getLabel();

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals("thingy", thirdInstruction.cst);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DUP, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);
		Label falseLabel = fifthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNULL, fifthInstruction.getOpcode());

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKEVIRTUAL, sixthInstruction.getOpcode());
		assertEquals("java/lang/String", sixthInstruction.owner);
		assertEquals("length", sixthInstruction.name);
		assertEquals("()I", sixthInstruction.desc);

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);
		Label trueLabel = seventhInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, seventhInstruction.getOpcode());

		LabelNode eighthInstruction = (LabelNode) instructions.get(7);

		assertEquals(falseLabel, eighthInstruction.getLabel());

		assertTrue(instructions.get(8) instanceof FrameNode);

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.POP, tenthInstruction.getOpcode());

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.ICONST_0, eleventhInstruction.getOpcode());

		LabelNode twelfthInstruction = (LabelNode) instructions.get(11);

		assertEquals(trueLabel, twelfthInstruction.getLabel());

		assertTrue(instructions.get(12) instanceof FrameNode);

		JumpInsnNode fourteenthInstruction = (JumpInsnNode) instructions.get(13);

		assertEquals(Opcodes.IFEQ, fourteenthInstruction.getOpcode());
		Label secondLabel = fourteenthInstruction.label.getLabel();

		InsnNode fifteenthInstruction = (InsnNode) instructions.get(14);

		assertEquals(Opcodes.ICONST_1, fifteenthInstruction.getOpcode());

		JumpInsnNode sixteenthInstruction = (JumpInsnNode) instructions.get(15);

		assertEquals(Opcodes.IFEQ, sixteenthInstruction.getOpcode());
		Label thirdLabel = sixteenthInstruction.label.getLabel();

		InsnNode seventeenthInstruction = (InsnNode) instructions.get(16);

		assertEquals(Opcodes.ICONST_1, seventeenthInstruction.getOpcode());

		VarInsnNode eighteenthInstruction = (VarInsnNode) instructions.get(17);

		assertEquals(Opcodes.ISTORE, eighteenthInstruction.getOpcode());
		assertEquals(1, eighteenthInstruction.var);

		LabelNode nineteenthInstruction = (LabelNode) instructions.get(18);

		assertEquals(firstLabel, nineteenthInstruction.getLabel());
		assertEquals(secondLabel, nineteenthInstruction.getLabel());
		assertEquals(thirdLabel, nineteenthInstruction.getLabel());
	}

	@Test
	public void testMultipleOrs()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"if (this || \"thingy\" || 1)\n" +
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

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(23, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		assertEquals(Opcodes.IFNONNULL, secondInstruction.getOpcode());
		Label firstLabel = secondInstruction.label.getLabel();

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals("thingy", thirdInstruction.cst);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DUP, fourthInstruction.getOpcode());

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);
		Label falseLabel = fifthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNULL, fifthInstruction.getOpcode());

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKEVIRTUAL, sixthInstruction.getOpcode());
		assertEquals("java/lang/String", sixthInstruction.owner);
		assertEquals("length", sixthInstruction.name);
		assertEquals("()I", sixthInstruction.desc);

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);
		Label trueLabel = seventhInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, seventhInstruction.getOpcode());

		LabelNode eighthInstruction = (LabelNode) instructions.get(7);

		assertEquals(falseLabel, eighthInstruction.getLabel());

		assertTrue(instructions.get(8) instanceof FrameNode);

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.POP, tenthInstruction.getOpcode());

		InsnNode eleventhInstruction = (InsnNode) instructions.get(10);

		assertEquals(Opcodes.ICONST_0, eleventhInstruction.getOpcode());

		LabelNode twelfthInstruction = (LabelNode) instructions.get(11);

		assertEquals(trueLabel, twelfthInstruction.getLabel());

		assertTrue(instructions.get(12) instanceof FrameNode);

		JumpInsnNode fourteenthInstruction = (JumpInsnNode) instructions.get(13);

		assertEquals(Opcodes.IFNE, fourteenthInstruction.getOpcode());
		Label secondLabel = fourteenthInstruction.label.getLabel();

		InsnNode fifteenthInstruction = (InsnNode) instructions.get(14);

		assertEquals(Opcodes.ICONST_1, fifteenthInstruction.getOpcode());

		JumpInsnNode sixteenthInstruction = (JumpInsnNode) instructions.get(15);

		assertEquals(Opcodes.IFEQ, sixteenthInstruction.getOpcode());
		Label thirdLabel = sixteenthInstruction.label.getLabel();

		LabelNode seventeenthInstruction = (LabelNode) instructions.get(16);

		assertEquals(firstLabel, seventeenthInstruction.getLabel());
		assertEquals(secondLabel, seventeenthInstruction.getLabel());

		assertTrue(instructions.get(17) instanceof FrameNode);

		InsnNode nineteenthInstruction = (InsnNode) instructions.get(18);

		assertEquals(Opcodes.ICONST_1, nineteenthInstruction.getOpcode());

		VarInsnNode twentiethInstruction = (VarInsnNode) instructions.get(19);

		assertEquals(Opcodes.ISTORE, twentiethInstruction.getOpcode());
		assertEquals(1, twentiethInstruction.var);

		LabelNode twentyFirstInstruction = (LabelNode) instructions.get(20);

		assertEquals(thirdLabel, twentyFirstInstruction.getLabel());
	}

	@Test
	public void testObjectAndNumber()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"if (this && 0)\n" +
					"{\n" +
					"	Num one = 1.0;\n" +
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

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(9, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);
		Label leftLabel = secondInstruction.label.getLabel();

		assertEquals(Opcodes.IFNULL, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.ICONST_0, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label rightLabel = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DCONST_1, fifthInstruction.getOpcode());

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.DSTORE, sixthInstruction.getOpcode());
		assertEquals(1, sixthInstruction.var);

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(leftLabel, seventhInstruction.getLabel());
		assertEquals(rightLabel, seventhInstruction.getLabel());

		assertTrue(instructions.get(7) instanceof FrameNode);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.RETURN, ninthInstruction.getOpcode());
	}

	@Test
	public void testAssignStringAndDouble()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"Object obj = \"thingy\" && 0.0;\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(20, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DUP, thirdInstruction.getOpcode());

		JumpInsnNode fourthInstruction = (JumpInsnNode) instructions.get(3);
		Label falseLabel = fourthInstruction.label.getLabel();

		assertEquals(Opcodes.IFNULL, fourthInstruction.getOpcode());

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEVIRTUAL, fifthInstruction.getOpcode());
		assertEquals("java/lang/String", fifthInstruction.owner);
		assertEquals("length", fifthInstruction.name);
		assertEquals("()I", fifthInstruction.desc);

		JumpInsnNode sixthInstruction = (JumpInsnNode) instructions.get(5);
		Label trueLabel = sixthInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, sixthInstruction.getOpcode());

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);

		assertEquals(falseLabel, seventhInstruction.getLabel());

		assertTrue(instructions.get(7) instanceof FrameNode);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.POP, ninthInstruction.getOpcode());

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.ICONST_0, tenthInstruction.getOpcode());

		LabelNode eleventhInstruction = (LabelNode) instructions.get(10);

		assertEquals(trueLabel, eleventhInstruction.getLabel());

		assertTrue(instructions.get(11) instanceof FrameNode);

		JumpInsnNode thirteenthInstruction = (JumpInsnNode) instructions.get(12);
		Label label = thirteenthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, thirteenthInstruction.getOpcode());

		InsnNode fourteenthInstruction = (InsnNode) instructions.get(13);

		assertEquals(Opcodes.POP, fourteenthInstruction.getOpcode());

		InsnNode fifteenthInstruction = (InsnNode) instructions.get(14);

		assertEquals(Opcodes.DCONST_0, fifteenthInstruction.getOpcode());

		MethodInsnNode sixteenthInstruction = (MethodInsnNode) instructions.get(15);

		assertEquals(Opcodes.INVOKESTATIC, sixteenthInstruction.getOpcode());
		assertEquals("java/lang/Double", sixteenthInstruction.owner);
		assertEquals("valueOf", sixteenthInstruction.name);
		assertEquals("(D)Ljava/lang/Double;", sixteenthInstruction.desc);

		LabelNode seventeenthInstruction = (LabelNode) instructions.get(16);

		assertEquals(label, seventeenthInstruction.getLabel());

		assertTrue(instructions.get(17) instanceof FrameNode);

		VarInsnNode nineteenthInstruction = (VarInsnNode) instructions.get(18);

		assertEquals(Opcodes.ASTORE, nineteenthInstruction.getOpcode());
		assertEquals(1, nineteenthInstruction.var);
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

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(2, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
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

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DADD, thirdInstruction.getOpcode());
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

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(2, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
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

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DSUB, thirdInstruction.getOpcode());
	}

	@Test
	public void testPredecrementInRecursion()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method(Num i)\n" +
				"{\n" +
					"return this.method(--i);\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(7, instructions.size());

		LabelNode firstInstruction = (LabelNode) instructions.get(0);
		Label label = firstInstruction.getLabel();

		assertTrue(instructions.get(1) instanceof FrameNode);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.DLOAD, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCONST_1, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DSUB, fifthInstruction.getOpcode());

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.DSTORE, sixthInstruction.getOpcode());
		assertEquals(1, sixthInstruction.var);

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);

		assertEquals(Opcodes.GOTO, seventhInstruction.getOpcode());
		assertEquals(label, seventhInstruction.label.getLabel());
	}

	@Test
	public void testPredecrementOfOptimisedVariable()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"Num i = 1;\n" +
					"this.less(--i);\n" +
				"}\n" +
				"public Void less(Num number)\n" +
				"{\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(9, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.ICONST_1, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ISUB, fourthInstruction.getOpcode());
	}

	@Test
	public void testPredecrementOfNotOptimisedVariable()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"Num i = 1;\n" +
					"this.less(--i);\n" +
					"this.less(i);\n" +
				"}\n" +
				"public Void less(Num number)\n" +
				"{\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(19, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.ISTORE, secondInstruction.getOpcode());
		assertEquals(1, secondInstruction.var);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ILOAD, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.ICONST_1, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.ISUB, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DUP, seventhInstruction.getOpcode());

		VarInsnNode eighthInstruction = (VarInsnNode) instructions.get(7);

		assertEquals(Opcodes.ISTORE, eighthInstruction.getOpcode());
		assertEquals(1, eighthInstruction.var);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.I2D, ninthInstruction.getOpcode());

		assertTrue(instructions.get(9) instanceof LabelNode);
		assertTrue(instructions.get(10) instanceof LineNumberNode);

		InvokeDynamicInsnNode twelfthInstruction =
			(InvokeDynamicInsnNode) instructions.get(11);

		assertEquals(Opcodes.INVOKEDYNAMIC, twelfthInstruction.getOpcode());
		assertEquals("less", twelfthInstruction.name);
		assertEquals("(LMyClass;D)V", twelfthInstruction.desc);

		VarInsnNode thirteenthInstruction = (VarInsnNode) instructions.get(12);

		assertEquals(Opcodes.ALOAD, thirteenthInstruction.getOpcode());
		assertEquals(0, thirteenthInstruction.var);

		VarInsnNode fourteenthInstruction = (VarInsnNode) instructions.get(13);

		assertEquals(Opcodes.ILOAD, fourteenthInstruction.getOpcode());
		assertEquals(1, fourteenthInstruction.var);
	}

	@Test
	public void testPostdecrementInRecursion()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method(Num i)\n" +
				"{\n" +
					"return this.method(i--);\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(method.name, "method");
		assertEquals(5, instructions.size());

		LabelNode firstInstruction = (LabelNode) instructions.get(0);
		Label label = firstInstruction.getLabel();

		assertTrue(instructions.get(1) instanceof FrameNode);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.DLOAD, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.DSTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);

		assertEquals(Opcodes.GOTO, fifthInstruction.getOpcode());
		assertEquals(label, fifthInstruction.label.getLabel());
	}

	@Test
	public void testPostdecrementOfOptimisedVariable()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"Num i = 1;\n" +
					"this.less(i--);\n" +
				"}\n" +
				"public Void less(Num number)\n" +
				"{\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(7, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.I2D, thirdInstruction.getOpcode());

		assertTrue(instructions.get(3) instanceof LabelNode);
		assertTrue(instructions.get(4) instanceof LineNumberNode);

		InvokeDynamicInsnNode sixthInstruction =
			(InvokeDynamicInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKEDYNAMIC, sixthInstruction.getOpcode());
		assertEquals("less", sixthInstruction.name);
	}
}
