package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
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
		assertEquals(2, eleventhInstruction.var);

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

		assertEquals(20, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("java.lang.String", secondInstruction.cst);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("cacheObjectSignature", thirdInstruction.name);
		assertEquals("yirgacheffe/lang/Bootstrap", thirdInstruction.owner);
		assertEquals("(Ljava/lang/Object;Ljava/lang/String;)V", thirdInstruction.desc);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ICONST_0, fourthInstruction.getOpcode());

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ISTORE, fifthInstruction.getOpcode());
		assertEquals(2, fifthInstruction.var);

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		Label continueLabel = sixthInstruction.getLabel();

		assertTrue(instructions.get(6) instanceof FrameNode);

		VarInsnNode eighthInstruction = (VarInsnNode) instructions.get(7);

		assertEquals(Opcodes.ILOAD, eighthInstruction.getOpcode());
		assertEquals(2, eighthInstruction.var);

		VarInsnNode ninthInstruction = (VarInsnNode) instructions.get(8);

		assertEquals(Opcodes.ALOAD, ninthInstruction.getOpcode());
		assertEquals(1, ninthInstruction.var);

		assertTrue(instructions.get(9) instanceof LabelNode);
		assertTrue(instructions.get(10) instanceof LineNumberNode);
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
		assertEquals(3, eighthInstruction.var);

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

		assertEquals(13, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("java.lang.String", secondInstruction.cst);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("cacheObjectSignature", thirdInstruction.name);
		assertEquals("yirgacheffe/lang/Bootstrap", thirdInstruction.owner);
		assertEquals("(Ljava/lang/Object;Ljava/lang/String;)V", thirdInstruction.desc);

		LdcInsnNode fourthInstruction = (LdcInsnNode) instructions.get(3);

		assertEquals(Opcodes.LDC, fourthInstruction.getOpcode());
		assertEquals("thingy", fourthInstruction.cst);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ASTORE, fifthInstruction.getOpcode());
		assertEquals(2, fifthInstruction.var);

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		Label label = sixthInstruction.getLabel();

		assertTrue(instructions.get(6) instanceof FrameNode);

		LdcInsnNode eighthInstruction = (LdcInsnNode) instructions.get(7);

		assertEquals(Opcodes.LDC, eighthInstruction.getOpcode());
		assertEquals("sumpt", eighthInstruction.cst);

		VarInsnNode ninthInstruction = (VarInsnNode) instructions.get(8);

		assertEquals(Opcodes.ASTORE, ninthInstruction.getOpcode());
		assertEquals(2, ninthInstruction.var);

		JumpInsnNode tenthInstruction = (JumpInsnNode) instructions.get(9);

		assertEquals(Opcodes.GOTO, tenthInstruction.getOpcode());
		assertEquals(label, tenthInstruction.label.getLabel());
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

	@Test
	public void testUpdateValueInLoop()
	{
		String source =
			"import java.util.Map;\n" +
			"import java.util.HashMap;\n" +
			"class MyClass\n" +
			"{\n" +
				"Map<String, Num> numeralValues = new HashMap<String, Num>();\n" +
				"private MyClass()\n" +
				"{\n" +
					"this.numeralValues.put(\"I\", 1);\n" +
				"}\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"this.toNumber(\"I\");\n" +
				"}\n" +
				"private Num toNumber(String string)\n" +
				"{\n" +
					"Num total = 0;\n" +
					"for (Num i = 0; i < 1; i++)\n" +
					"{\n" +
						"Num value = this.numeralValues.get(\"I\");\n" +
						"total = total - value;\n" +
					"}\n" +
					"return total;" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode fifthMethod = classNode.methods.get(4);

		assertEquals("toNumber", fifthMethod.name);

		InsnList instructions = fifthMethod.instructions;

		assertEquals(28, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_0, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.I2D, secondInstruction.getOpcode());

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.DSTORE, thirdInstruction.getOpcode());
		assertEquals(2, thirdInstruction.var);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.ICONST_0, fourthInstruction.getOpcode());

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ISTORE, fifthInstruction.getOpcode());
		assertEquals(4, fifthInstruction.var);

		assertTrue(instructions.get(5) instanceof LabelNode);
		assertTrue(instructions.get(6) instanceof FrameNode);

		VarInsnNode eighthInstruction = (VarInsnNode) instructions.get(7);

		assertEquals(Opcodes.ILOAD, eighthInstruction.getOpcode());
		assertEquals(4, eighthInstruction.var);

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.ICONST_1, ninthInstruction.getOpcode());

		JumpInsnNode tenthInstruction = (JumpInsnNode) instructions.get(9);
		Label forLabel = tenthInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ICMPGE, tenthInstruction.getOpcode());

		VarInsnNode eleventhInstruction = (VarInsnNode) instructions.get(10);

		assertEquals(Opcodes.DLOAD, eleventhInstruction.getOpcode());
		assertEquals(2, eleventhInstruction.var);

		VarInsnNode twelfthInstruction = (VarInsnNode) instructions.get(11);

		assertEquals(Opcodes.ALOAD, twelfthInstruction.getOpcode());
		assertEquals(0, twelfthInstruction.var);

		assertTrue(instructions.get(12) instanceof LabelNode);
		assertTrue(instructions.get(13) instanceof LineNumberNode);

		FieldInsnNode fifteenthInstruction = (FieldInsnNode) instructions.get(14);

		assertEquals(Opcodes.GETFIELD, fifteenthInstruction.getOpcode());
		assertEquals("numeralValues", fifteenthInstruction.name);

		LdcInsnNode sixteenthInstruction = (LdcInsnNode) instructions.get(15);

		assertEquals(Opcodes.LDC, sixteenthInstruction.getOpcode());
		assertEquals("I", sixteenthInstruction.cst);

		assertTrue(instructions.get(16) instanceof LabelNode);
		assertTrue(instructions.get(17) instanceof LineNumberNode);

		InvokeDynamicInsnNode nineteenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(18);

		assertEquals(Opcodes.INVOKEDYNAMIC, nineteenthInstruction.getOpcode());
		assertEquals("get", nineteenthInstruction.name);
	}
}
