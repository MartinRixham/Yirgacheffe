package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstructorListenerTest
{
	@Test
	public void testMainClassHasDefaultConstructor()
	{
		String source = "class MyClass { main method(Array<String> args) {} }";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(3, classNode.methods.size());

		MethodNode constructor = classNode.methods.get(2);

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

	@Test
	public void testConstructorWithNumberParameter()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass(Num param) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(2, classNode.methods.size());

		MethodNode constructor = classNode.methods.get(0);

		assertEquals("(D)V", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, constructor.access);
		assertEquals("<init>", constructor.name);

		InsnList instructions = constructor.instructions;

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESPECIAL, secondInstruction.getOpcode());
		assertEquals("java/lang/Object", secondInstruction.owner);
		assertEquals("<init>", secondInstruction.name);
		assertEquals("()V", secondInstruction.desc);
	}

	@Test
	public void testConstructorWithMissingModifier()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"MyClass(Num param) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Expected public or private access modifier " +
				"at start of constructor declaration.\n",
			result.getErrors());
	}

	@Test
	public void testConstructorWithWrongName()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClasss(Num param) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:7 Constructor of incorrect type MyClasss: expected MyClass.\n",
			result.getErrors());
	}

	@Test
	public void testPrivateConstructor()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"private MyClass(String param) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(2, classNode.methods.size());

		MethodNode constructor = classNode.methods.get(0);

		assertEquals("(Ljava/lang/String;)V", constructor.desc);
		assertEquals(Opcodes.ACC_PRIVATE, constructor.access);
		assertEquals("<init>", constructor.name);
	}

	@Test
	public void testConstructorCallsInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy = \"sumpt\";\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(3, classNode.methods.size());

		MethodNode constructor = classNode.methods.get(1);
		InsnList instructions = constructor.instructions;

		assertEquals(7, instructions.size());

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("MyClass", fourthInstruction.owner);
		assertEquals("0init_field_thingy", fourthInstruction.name);
		assertEquals("()V", fourthInstruction.desc);
	}

	@Test
	public void testCallConstructorFromAnother()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{" +
					"this(\"\");" +
				"}\n" +
				"public MyClass(String param) {}\n" +
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

		assertEquals(4, classNode.methods.size());

		MethodNode firstConstructor = classNode.methods.get(0);

		assertEquals("()V", firstConstructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, firstConstructor.access);
		assertEquals("<init>", firstConstructor.name);

		InsnList instructions = firstConstructor.instructions;

		assertEquals(5, instructions.size());

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

		InvokeDynamicInsnNode fourthInstruction =
			(InvokeDynamicInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourthInstruction.getOpcode());
		assertEquals("0this", fourthInstruction.name);
		assertEquals("(LMyClass;)V", fourthInstruction.desc);

		this.testConstructionMethod(classNode.methods.get(1));

		MethodNode secondConstructor = classNode.methods.get(2);

		assertEquals("(Ljava/lang/String;)V", secondConstructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC, secondConstructor.access);
		assertEquals("<init>", secondConstructor.name);
	}

	private void testConstructionMethod(MethodNode method)
	{
		assertEquals("()V", method.desc);
		assertEquals(Opcodes.ACC_PRIVATE, method.access);
		assertEquals("0this", method.name);

		InsnList instructions = method.instructions;

		assertEquals(6, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("", secondInstruction.cst);

		assertTrue(instructions.get(2) instanceof LabelNode);
		assertTrue(instructions.get(3) instanceof LineNumberNode);

		InvokeDynamicInsnNode sixthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, sixthInstruction.getOpcode());
		assertEquals("0this", sixthInstruction.name);
		assertEquals("(LMyClass;Ljava/lang/String;)V", sixthInstruction.desc);
	}

	@Test
	public void testCallThisFromMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"this();\n" +
				"}\n" +
				"public MyClass(){}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 5:0 Cannot call this() outside of constructor.\n",
			result.getErrors());
	}

	@Test
	public void testCallDelegateFromMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"delegate(1);\n" +
				"}\n" +
				"public MyClass(){}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 5:0 Cannot set delegate outside of constructor.\n",
			result.getErrors());
	}

	@Test
	public void testBranchDoesNotDelegate()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (false)" +
					"{\n" +
						"return;\n" +
					"}\n" +
					"delegate(\"\");\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 1:0 Missing implementation of interface method " +
				"Num compareTo(java.lang.String).\n",
			result.getErrors());
	}

	@Test
	public void testBranchDoesNotDelegateTwoInterface()
	{
		String source =
			"class MyClass implements Comparable<String>, Runnable\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (false)" +
					"{\n" +
						"delegate(\"\");\n" +
						"return;\n" +
					"}\n" +
					"delegate(this);\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 1:0 Missing implementation of interface method " +
				"Void run().\n",
			result.getErrors());
	}

	@Test
	public void testBranchWithElseDoesNotDelegate()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (true)\n" +
					"{\n" +
						"return;\n" +
					"}\n" +
					"else\n" +
					"{\n" +
					"}\n" +
					"delegate(\"\");\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 1:0 Missing implementation of interface method " +
				"Num compareTo(java.lang.String).\n",
			result.getErrors());
	}

	@Test
	public void testDelegationBeforeBranch()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"delegate(\"\");\n" +
					"if (false)" +
					"{\n" +
						"return;\n" +
					"}\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testDelegationByThisBeforeBranch()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"this();\n" +
					"if (false)" +
					"{\n" +
						"return;\n" +
					"}\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testDelegationByThisBeforeNestedBranches()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"this();\n" +
					"if (false)" +
					"{\n" +
						"if (false)" +
						"{\n" +
							"return;\n" +
						"}\n" +
					"}\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testInterfaceNotDelegatedFromIf()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (true)" +
					"{\n" +
						"delegate(\"\");\n" +
					"}\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 1:0 Missing implementation of interface method " +
				"Num compareTo(java.lang.String).\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceNotDelegatedFromElse()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (true)" +
					"{\n" +
					"}\n" +
					"else\n" +
					"{\n" +
						"delegate(\"\");\n" +
					"}\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 1:0 Missing implementation of interface method " +
				"Num compareTo(java.lang.String).\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceNotDelegatedFromBranch()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (false)" +
					"{\n" +
						"delegate(\"\");\n" +
					"}\n" +
					"else\n" +
					"{\n" +
					"}\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 1:0 Missing implementation of interface method " +
				"Num compareTo(java.lang.String).\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceDelegatedFromBothBranches()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (false)" +
					"{\n" +
						"delegate(\"thingy\");\n" +
					"}\n" +
					"else\n" +
					"{\n" +
						"delegate(\"sumpt\");\n" +
					"}\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testConstructorCallsLaterInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass() {}\n" +
				"String thingy = \"sumpt\";\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(3, classNode.methods.size());

		MethodNode constructor = classNode.methods.get(0);
		InsnList instructions = constructor.instructions;

		assertEquals(7, instructions.size());

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("MyClass", fourthInstruction.owner);
		assertEquals("0init_field_thingy", fourthInstruction.name);
		assertEquals("()V", fourthInstruction.desc);
	}

	@Test
	public void testErrorOnUninitialisedField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:17 Constructor MyClass() does not initialise field 'thingy'.\n",
			result.getErrors());
	}

	@Test
	public void testErrorOnUninitialisedFieldWithMainMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"main method(Array<String> args) {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Default constructor does not initialise field 'thingy'.\n",
			result.getErrors());
	}

	@Test
	public void testBranchDoesNotInitialiseField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"String sumpt;\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (true)\n" +
					"{\n" +
						"this.thingy = \"thingy\";" +
						"return;\n" +
					"}\n" +
					"this.sumpt = \"sumpt\";" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 6:0 Constructor MyClass() does not initialise field 'sumpt'.\n" +
			"line 6:0 Constructor MyClass() does not initialise field 'thingy'.\n",
			result.getErrors());
	}

	@Test
	public void testInitialiseFieldAfterBranch()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String sumpt;\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (true)\n" +
					"{\n" +
					"}\n" +
					"else\n" +
					"{\n" +
					"}\n" +
					"this.sumpt = \"sumpt\";" +
				"}\n" +
			"}";

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
	public void testBranchesDoNotInitialiseField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"String sumpt;\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (true)\n" +
					"{\n" +
						"this.thingy = \"thingy\";" +
						"return;\n" +
					"}\n" +
					"else if (true)\n" +
					"{\n" +
						"this.sumpt = \"sumpt\";" +
						"return;\n" +
					"}\n" +
					"this.thingy = \"thingy\";" +
					"this.sumpt = \"sumpt\";" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 6:0 Constructor MyClass() does not initialise field 'sumpt'.\n" +
				"line 6:0 Constructor MyClass() does not initialise field 'thingy'.\n",
			result.getErrors());
	}

	@Test
	public void testInitialiseFieldViaAnotherConstructor()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"public MyClass()\n" +
				"{\n" +
					"this(\"thingy\");\n" +
				"}\n" +
				"public MyClass(String string)\n" +
				"{\n" +
					"this.thingy = string;\n" +
				"}\n" +
			"}";

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
	public void testSelfInstantiationWithWrongParameters()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"this(\"thingy\");\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:0 Invoked constructor MyClass(java.lang.String) not found.\n",
			result.getErrors());
	}

	@Test
	public void testCallOwnConstructorFromBranch()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (false)\n" +
					"{\n" +
						"this(\"thingy\");\n" +
					"}\n" +
				"}\n" +
				"public MyClass(String string)\n" +
				"{\n" +
					"this.thingy = string;\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:0 Constructor MyClass() does not initialise field 'thingy'.\n",
			result.getErrors());
	}

	@Test
	public void testInitialiseFieldWithAssignmentOrConstructorCall()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (false)\n" +
					"{\n" +
						"this(\"thingy\");\n" +
					"}\n" +
					"else\n" +
					"{\n" +
						"this.thingy = \"thingy\";\n" +
					"}\n" +
				"}\n" +
				"public MyClass(String string)\n" +
				"{\n" +
					"this.thingy = string;\n" +
				"}\n" +
			"}";

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
	public void testInitialiseFieldOnBranch()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"String sumpt;\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (false)\n" +
					"{\n" +
						"this.thingy = \"thingy\";\n" +
						"this.sumpt = \"sumpt\";\n" +
						"return;\n" +
					"}\n" +
					"this.thingy = \"thingy\";\n" +
					"this.sumpt = \"sumpt\";\n" +
				"}\n" +
			"}";

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
	public void testInitialiseBeforeConditionReturn()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"String sumpt;\n" +
				"public MyClass()\n" +
				"{\n" +
					"this.thingy = \"thingy\";\n" +
					"this.sumpt = \"sumpt\";\n" +
					"if (false)\n" +
					"{\n" +
						"return;\n" +
					"}\n" +
				"}\n" +
			"}";

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
	public void testDelegateInterfaceImplementation()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"delegate(\"thingy\");\n" +
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

		assertEquals(1, classNode.fields.size());

		FieldNode field = classNode.fields.get(0);

		assertEquals("0delegate", field.name);
		assertEquals("Ljava/lang/Object;", field.desc);

		assertEquals(3, classNode.methods.size());

		MethodNode constructor = classNode.methods.get(1);

		this.checkConstructorInstructions(constructor.instructions);

		MethodNode method = classNode.methods.get(2);

		assertEquals("compareTo", method.name);
		assertEquals("(Ljava/lang/Object;)I", method.desc);

		InsnList instructions = method.instructions;

		assertEquals(6, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		FieldInsnNode secondInstruction = (FieldInsnNode) instructions.get(1);

		assertEquals(Opcodes.GETFIELD, secondInstruction.getOpcode());
		assertEquals("0delegate", secondInstruction.name);
		assertEquals("MyClass", secondInstruction.owner);
		assertEquals("Ljava/lang/Object;", secondInstruction.desc);

		TypeInsnNode thirdInstruction = (TypeInsnNode) instructions.get(2);

		assertEquals(Opcodes.CHECKCAST, thirdInstruction.getOpcode());
		assertEquals("java/lang/Comparable", thirdInstruction.desc);

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ALOAD, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("compareTo", fifthInstruction.name);

		assertEquals(
			"(Ljava/lang/Comparable;Ljava/lang/Object;)I",
			fifthInstruction.desc);
	}

	private void checkConstructorInstructions(InsnList instructions)
	{
		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("thingy", secondInstruction.cst);

		FieldInsnNode thirdInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.PUTFIELD, thirdInstruction.getOpcode());
		assertEquals("0delegate", thirdInstruction.name);
		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("Ljava/lang/Object;", thirdInstruction.desc);
	}

	@Test
	public void testDelegateWithNoArguments()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"delegate();\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals("line 5:0 Delegate has one parameter.\n", result.getErrors());
	}

	@Test
	public void testDelegateInterfaceImplementationForMultipleConstructors()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"delegate(\"\");\n" +
				"}\n" +
				"public MyClass(String string)\n" +
				"{\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Missing implementation of interface method " +
				"Num compareTo(java.lang.String).\n",
			result.getErrors());
	}

	@Test
	public void testDelegateInterfaceImplementedByThisCall()
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"delegate(\"\");\n" +
				"}\n" +
				"public MyClass(String string)\n" +
				"{\n" +
					"this();\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testInterfaceConstructorCallsItself()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"MyInterface()\n" +
				"{\n" +
					"return this();" +
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

		MethodNode constructor = classNode.methods.get(0);

		InsnList instructions = constructor.instructions;

		assertEquals(4, instructions.size());

		assertTrue(instructions.get(0) instanceof LabelNode);
		assertTrue(instructions.get(1) instanceof LineNumberNode);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("MyInterface", thirdInstruction.owner);
		assertEquals("0this", thirdInstruction.name);
		assertEquals("()LMyInterface;", thirdInstruction.desc);
	}

	@Test
	public void testInterfaceConstructorCannotBePrivate()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"private MyInterface()\n" +
				"{\n" +
					"return this();" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 3:0 Access modifier is not required " +
				"for interface constructor declaration.\n",
			result.getErrors());
	}

	@Test
	public void testCallingInterfaceConstructor()
	{
		String interfaceSource =
			"interface MyInterface\n" +
			"{\n" +
				"MyInterface()\n" +
				"{\n" +
					"return this();\n" +
				"}\n" +
			"}";

		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"new MyInterface();\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();

		new Compiler("", interfaceSource).compileInterface(classes);

		classes.clearCache();

		Compiler interfaceCompiler = new Compiler("", interfaceSource);
		CompilationResult interfaceResult = interfaceCompiler.compile(classes);

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(classes);

		assertTrue(interfaceResult.isSuccessful());
		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode constructor = classNode.methods.get(0);

		InsnList instructions = constructor.instructions;

		assertEquals(8, instructions.size());

		assertTrue(instructions.get(3) instanceof LabelNode);
		assertTrue(instructions.get(4) instanceof LineNumberNode);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESTATIC, thirdInstruction.getOpcode());
		assertEquals("MyInterface", thirdInstruction.owner);
		assertEquals("0this", thirdInstruction.name);
		assertEquals("()LMyInterface;", thirdInstruction.desc);
	}

	@Test
	public void testDelegateInterfaceWithNumberParameter()
	{
		String source =
			"class MyClass implements Comparable<Num>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"delegate(1);\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testInterfaceWithConstructor()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"MyInterface()\n" +
				"{\n" +
					"return new MutableReference<MyInterface>().get();" +
				"}\n" +
			"}\n";

		Compiler compiler = new Compiler("MyInterface.yg", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(1, classNode.methods.size());

		MethodNode constructor = classNode.methods.get(0);

		assertEquals("0this", constructor.name);
		assertEquals("()LMyInterface;", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, constructor.access);
	}

	@Test
	public void testInterfaceWithConstructorWithMissingReturn()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"MyInterface()\n" +
				"{\n" +
				"}\n" +
			"}\n";

		Compiler compiler = new Compiler("MyInterface.yg", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals("line 5:0 Missing return statement.\n", result.getErrors());
	}

	@Test
	public void testInterfaceWithConstructorWithThisRead()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"MyInterface()\n" +
				"{\n" +
					"return this;\n" +
				"}\n" +
			"}\n";

		Compiler compiler = new Compiler("MyInterface.yg", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:7 Cannot reference 'this' in interface constructor.\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceWithConstructorWithVariableRead()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"MyInterface(MyInterface other)\n" +
				"{\n" +
					"return other;\n" +
				"}\n" +
			"}\n";

		Compiler compiler = new Compiler("MyInterface.yg", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals(1, classNode.methods.size());

		MethodNode constructor = classNode.methods.get(0);

		assertEquals("0this", constructor.name);
		assertEquals("(LMyInterface;)LMyInterface;", constructor.desc);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, constructor.access);

		InsnList instructions = constructor.instructions;

		assertEquals(2, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);
	}
}
