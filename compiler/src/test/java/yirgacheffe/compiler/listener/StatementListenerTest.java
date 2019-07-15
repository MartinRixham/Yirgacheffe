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
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class StatementListenerTest
{
	@Test
	public void testLocalVariableDeclaration()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"Num myVariable;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testMissingSemicolon()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"Num myVariable\n" +
					"Num anotherVariable\n" +
					"new String()\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:0 Missing ';'.\n" +
			"line 6:0 Missing ';'.\n" +
			"line 7:0 Missing ';'.\n",
			result.getErrors());
	}

	@Test
	public void testAssignParameterToLocalVariable()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method(Num param)" +
				"{\n" +
					"Num myVariable = param;\n" +
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

		assertEquals(3, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		VarInsnNode secondInstruction = (VarInsnNode) instructions.get(1);

		assertEquals(Opcodes.DSTORE, secondInstruction.getOpcode());
		assertEquals(3, secondInstruction.var);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.RETURN, thirdInstruction.getOpcode());
	}

	@Test
	public void testUninitialisedVariable()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"myVariable = 1;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Assignment to uninitialised variable 'myVariable'.\n",
			result.getErrors());
	}

	@Test
	public void testWriteVariableInsideBlock()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"{" +
						"String thingy = \"thingy\";\n" +
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

		assertEquals(3, instructions.size());
	}

	@Test
	public void testAssignToVariableInParentBlock()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"Num myVariable;\n" +
					"{" +
						"myVariable = 50;" +
					"}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testConditionalStatements()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"if (true)" +
					"{" +
						"Num one = 1.0;\n" +
					"}\n" +
					"else if (false)" +
					"{" +
						"Num two = 2.0;" +
					"}" +
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

		assertEquals(14, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);

		Label ifLabel = secondInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.DSTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		JumpInsnNode fifthInstruction = (JumpInsnNode) instructions.get(4);

		Label elseLabel = fifthInstruction.label.getLabel();

		assertNotEquals(ifLabel, elseLabel);
		assertEquals(Opcodes.GOTO, fifthInstruction.getOpcode());

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(ifLabel, sixthInstruction.getLabel());

		assertTrue(instructions.get(6) instanceof FrameNode);

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.ICONST_0, eighthInstruction.getOpcode());

		JumpInsnNode ninthInstruction = (JumpInsnNode) instructions.get(8);

		Label elseIfLabel = ninthInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, ninthInstruction.getOpcode());

		LdcInsnNode tenthInstruction = (LdcInsnNode) instructions.get(9);

		assertEquals(Opcodes.LDC, tenthInstruction.getOpcode());
		assertEquals(2.0, tenthInstruction.cst);

		VarInsnNode eleventhInstruction = (VarInsnNode) instructions.get(10);

		assertEquals(Opcodes.DSTORE, eleventhInstruction.getOpcode());
		assertEquals(3, eleventhInstruction.var);

		LabelNode twelfthInstruction = (LabelNode) instructions.get(11);

		assertEquals(elseIfLabel, twelfthInstruction.getLabel());
		assertNotEquals(ifLabel, elseIfLabel);

		assertTrue(instructions.get(12) instanceof FrameNode);
	}

	@Test
	public void testIfEquation()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"if (true == false)" +
					"{" +
						"Num one = 1.0;\n" +
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

		assertEquals(8, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_0, secondInstruction.getOpcode());

		JumpInsnNode thirdInstruction = (JumpInsnNode) instructions.get(2);
		Label label = thirdInstruction.label.getLabel();

		assertEquals(Opcodes.IF_ICMPNE, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCONST_1, fourthInstruction.getOpcode());

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DSTORE, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);

		LabelNode sixthInstruction = (LabelNode) instructions.get(5);

		assertEquals(label, sixthInstruction.getLabel());
	}

	@Test
	public void testBranchWithoutReturnStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"if (true)" +
					"{" +
						"return 1;\n" +
					"}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 6:0 Missing return statement.\n", result.getErrors());
	}

	@Test
	public void testBranchWithReturnStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"if (true)" +
					"{" +
						"return 1;\n" +
					"}\n" +
					"else" +
					"{" +
						"return 2;\n" +
					"}\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testBranchWithEmptyReturnStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"if (true)" +
					"{" +
						"return;\n" +
					"}\n" +
					"else" +
					"{" +
						"return;\n" +
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

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		JumpInsnNode secondInstruction = (JumpInsnNode) instructions.get(1);
		Label falseLabel = secondInstruction.label.getLabel();

		assertEquals(Opcodes.IFEQ, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.RETURN, thirdInstruction.getOpcode());
	}

	@Test
	public void testMismatchedTypeAssignment()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"Num number = \"thingy\";" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Cannot assign expression of type " +
				"java.lang.String to variable of type Num.\n",
			result.getErrors());
	}

	@Test
	public void testReturnStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return 1000000.0;\n" +
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

		assertEquals(2, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		assertEquals(2, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(1000000.0, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testReturnException()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return new Exception();\n" +
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

		assertEquals(6, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/Exception", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		assertTrue(instructions.get(2) instanceof LabelNode);
		assertTrue(instructions.get(3) instanceof LineNumberNode);

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKESPECIAL, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.ATHROW, sixthInstruction.getOpcode());
	}

	@Test
	public void testHandleException()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{" +
					"Num number = try this.getNumber();\n" +
					"this.handle(number);" +
				"}\n" +
				"public Num getNumber()" +
				"{\n" +
					"return new Exception();\n" +
				"}\n" +
				"public Void handle(Num number) {}\n" +
				"public Void handle(Exception e) {}\n" +
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

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals("method", firstMethod.name);

		InsnList instructions = firstMethod.instructions;

		//assertEquals(15, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		assertTrue(instructions.get(1) instanceof LabelNode);
		assertTrue(instructions.get(2) instanceof LineNumberNode);

		InvokeDynamicInsnNode fourthInstruction =
			(InvokeDynamicInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourthInstruction.getOpcode());
		assertEquals("getNumber", fourthInstruction.name);

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEVIRTUAL, fifthInstruction.getOpcode());
		assertEquals("valueOf", fifthInstruction.name);

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.ASTORE, sixthInstruction.getOpcode());
		assertEquals(1, sixthInstruction.var);

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);
		Label label = seventhInstruction.label.getLabel();

		assertEquals(Opcodes.GOTO, seventhInstruction.getOpcode());

		VarInsnNode eighthInstruction = (VarInsnNode) instructions.get(7);

		assertEquals(Opcodes.ASTORE, eighthInstruction.getOpcode());
		assertEquals(1, eighthInstruction.var);

		LabelNode ninthInstruction = (LabelNode) instructions.get(8);

		assertEquals(label, ninthInstruction.getLabel());

		VarInsnNode tenthInstruction = (VarInsnNode) instructions.get(9);

		assertEquals(Opcodes.ALOAD, tenthInstruction.getOpcode());
		assertEquals(0, tenthInstruction.var);

		VarInsnNode eleventhInstruction = (VarInsnNode) instructions.get(10);

		assertEquals(Opcodes.ALOAD, eleventhInstruction.getOpcode());
		assertEquals(0, eleventhInstruction.var);

		assertTrue(instructions.get(11) instanceof LabelNode);
		assertTrue(instructions.get(12) instanceof LineNumberNode);

		InvokeDynamicInsnNode fourteenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(13);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourteenthInstruction.getOpcode());
		assertEquals("handle", fifthInstruction.name);
	}

	@Test
	public void testMismatchedReturnType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return \"thingy\";\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 4:0 Mismatched return type: " +
			"Cannot return expression of type " +
			"java.lang.String from method of return type Num.\n",
			result.getErrors());
	}

	@Test
	public void testMissingReturnStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public String method()" +
				"{\n" +
					"new String(\"thingy\");\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 5:0 Missing return statement.\n",
			result.getErrors());
	}

	@Test
	public void testEmptyReturnStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"return;\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

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
					"i++;\n" +
					"return i;\n" +
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

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DADD, thirdInstruction.getOpcode());

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.DSTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DLOAD, fifthInstruction.getOpcode());
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
					"++i;\n" +
					"return i;\n" +
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

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DADD, thirdInstruction.getOpcode());

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.DSTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DLOAD, fifthInstruction.getOpcode());
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
					"i--;\n" +
					"return i;\n" +
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

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DNEG, thirdInstruction.getOpcode());

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.DSTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DLOAD, fifthInstruction.getOpcode());
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
					"--i;\n" +
					"return i;\n" +
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

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.DLOAD, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.var);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DCONST_1, secondInstruction.getOpcode());

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DNEG, thirdInstruction.getOpcode());

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.DSTORE, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.DLOAD, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);
	}

	@Test
	public void testVoidVariableDeclaration()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"Void thingy;\n" +
				"}\n" +
			"}\n";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:0 Cannot declare variable of type Void.\n",
			result.getErrors());
	}
}
