package yirgacheffe.compiler.listener;

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
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
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FunctionCallListenerTest
{
	@Test
	public void testInstantiationStatement()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"new String();\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
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

		assertEquals(7, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/String", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);
		Label label = thirdInstruction.getLabel();

		LineNumberNode fourthInstruction = (LineNumberNode) instructions.get(3);

		assertEquals(label, fourthInstruction.start.getLabel());

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKESPECIAL, fifthInstruction.getOpcode());
		assertEquals("java/lang/String", fifthInstruction.owner);
		assertEquals("<init>", fifthInstruction.name);
		assertEquals("()V", fifthInstruction.desc);
		assertFalse(fifthInstruction.itf);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.POP, sixthInstruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.RETURN, seventhInstruction.getOpcode());
	}

	@Test
	public void testInstantiationWithParameter()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"new String(\"thingy\");\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
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

		assertEquals(8, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/String", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals("thingy", thirdInstruction.cst);

		LabelNode fourthInstruction = (LabelNode) instructions.get(3);
		Label label = fourthInstruction.getLabel();

		LineNumberNode fifthInstruction = (LineNumberNode) instructions.get(4);

		assertEquals(label, fifthInstruction.start.getLabel());

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESPECIAL, sixthInstruction.getOpcode());
		assertEquals("java/lang/String", sixthInstruction.owner);
		assertEquals("<init>", sixthInstruction.name);
		assertEquals("(Ljava/lang/String;)V", sixthInstruction.desc);
		assertFalse(sixthInstruction.itf);

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.POP, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.RETURN, eighthInstruction.getOpcode());
	}

	@Test
	public void testAnotherInstantiation()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"new Double(1.0);\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
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

		assertEquals(8, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/Double", firstInstruction.desc);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		LabelNode fourthInstruction = (LabelNode) instructions.get(3);
		Label label = fourthInstruction.getLabel();

		LineNumberNode fifthInstruction = (LineNumberNode) instructions.get(4);

		assertEquals(label, fifthInstruction.start.getLabel());

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESPECIAL, sixthInstruction.getOpcode());
		assertEquals("java/lang/Double", sixthInstruction.owner);
		assertEquals("<init>", sixthInstruction.name);
		assertEquals("(D)V", sixthInstruction.desc);
		assertFalse(sixthInstruction.itf);
	}

	@Test
	public void testInstantiationOfFullyQualifiedType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"new java.lang.String();\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testInstantiationOfUnknownType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"new NotAClass();\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:4 Unrecognised type: NotAClass is not a type.\n",
			result.getErrors());
	}

	@Test
	public void testInstantiationOfPrimitiveType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"new Bool(true);\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:4 Cannot instantiate primitive type Bool.\n",
			result.getErrors());
	}

	@Test
	public void testFunctionCall()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"\"thingy\".toString();\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals(1, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		assertEquals(6, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(label, thirdInstruction.start.getLabel());

		InvokeDynamicInsnNode fourthInstruction =
			(InvokeDynamicInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEDYNAMIC, fourthInstruction.getOpcode());
		assertEquals("toString", fourthInstruction.name);
		assertEquals(
			"(Ljava/lang/String;)Ljava/lang/String;",
			fourthInstruction.desc);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.POP, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.RETURN, sixthInstruction.getOpcode());
	}

	@Test
	public void testFunctionCallWithSubtypeArgument()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"\"thingy\".equals(\"sumpt\");\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
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

		assertEquals(7, instructions.size());

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("equals", fifthInstruction.name);
		assertEquals(
			"(Ljava/lang/String;Ljava/lang/Object;)Z",
			fifthInstruction.desc);
	}

	@Test
	public void testConstructorCallWithTypeParameter()
	{
		String source =
			"import java.lang.ref.WeakReference;\n" +
			"class MyClass\n" +
			"{\n" +
				"public Object method()" +
				"{\n" +
					"return new WeakReference<String>(\"thingy\");\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
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

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESPECIAL, sixthInstruction.getOpcode());
		assertEquals("java/lang/ref/WeakReference", sixthInstruction.owner);
		assertEquals("(Ljava/lang/Object;)V", sixthInstruction.desc);
		assertEquals("<init>", sixthInstruction.name);
		assertFalse(sixthInstruction.itf);
	}

	@Test
	public void testConstructorCallWithMismatchedTypeParameter()
	{
		String source =
			"import java.lang.ref.WeakReference;\n" +
			"class MyClass\n" +
			"{\n" +
				"public Object method()" +
				"{\n" +
					"return new WeakReference<System>(\"thingy\");\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:7 Argument of type java.lang.String cannot be assigned to " +
			"generic parameter of type yirgacheffe.lang.System.\n",
			result.getErrors());
	}

	@Test
	public void testConstructorWithMultipleTypeParameters()
	{
		String source =
			"import java.util.Map;\n" +
			"import java.util.HashMap;\n" +
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"Map<String, String> map = " +
						"new HashMap<String, Object>();\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 6:0 Cannot assign expression of type " +
				"java.util.HashMap<java.lang.String,java.lang.Object> " +
				"to variable of type java.util.Map<java.lang.String,java.lang.String>.\n",
			result.getErrors());
	}

	@Test
	public void testMethodWithMultipleTypeParameters()
	{
		String source =
			"import java.util.Map;\n" +
			"import java.util.HashMap;\n" +
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"Map<String, String> map = " +
						"new HashMap<String, String>();\n" +
					"map.put(\"thingy\", new Object());\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 7:3 Argument of type java.lang.Object cannot be assigned to " +
				"generic parameter of type java.lang.String.\n",
			result.getErrors());
	}

	@Test
	public void testNumberToString()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"123.0.toString();\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
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

		assertEquals(7, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(123.0, firstInstruction.cst);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("java/lang/Double", secondInstruction.owner);
		assertEquals("valueOf", secondInstruction.name);
		assertEquals("(D)Ljava/lang/Double;", secondInstruction.desc);
		assertFalse(secondInstruction.itf);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);
		Label label = thirdInstruction.getLabel();

		LineNumberNode fourthInstruction = (LineNumberNode) instructions.get(3);

		assertEquals(label, fourthInstruction.start.getLabel());

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("toString", fifthInstruction.name);
		assertEquals(
			"(Ljava/lang/Double;)Ljava/lang/String;",
			fifthInstruction.desc);
	}

	@Test
	public void testBooleanToString()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"true.toString();\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode firstMethod = classNode.methods.get(0);

		assertEquals(1, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		assertEquals(7, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("valueOf", secondInstruction.name);
		assertEquals("java/lang/Boolean", secondInstruction.owner);
		assertEquals("(Z)Ljava/lang/Boolean;", secondInstruction.desc);
		assertFalse(secondInstruction.itf);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);
		Label label = thirdInstruction.getLabel();

		LineNumberNode fourthInstruction = (LineNumberNode) instructions.get(3);

		assertEquals(label, fourthInstruction.start.getLabel());

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("toString", fifthInstruction.name);
		assertEquals(
			"(Ljava/lang/Boolean;)Ljava/lang/String;",
			fifthInstruction.desc);
	}

	@Test
	public void testIntToNumber()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return 1.23.intValue();\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
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

		assertEquals(7, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(1.23, firstInstruction.cst);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("java/lang/Double", secondInstruction.owner);
		assertEquals("valueOf", secondInstruction.name);
		assertEquals("(D)Ljava/lang/Double;", secondInstruction.desc);
		assertFalse(secondInstruction.itf);

		LabelNode thirdInstruction = (LabelNode) instructions.get(2);
		Label label = thirdInstruction.getLabel();

		LineNumberNode fourthInstruction = (LineNumberNode) instructions.get(3);

		assertEquals(label, fourthInstruction.start.getLabel());

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("intValue", fifthInstruction.name);
		assertEquals("(Ljava/lang/Double;)I", fifthInstruction.desc);

		InsnNode sixthInsruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.I2D, sixthInsruction.getOpcode());

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.DRETURN, seventhInstruction.getOpcode());
	}

	@Test
	public void testLongToNumber()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return 1.23.longValue();\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
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

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("longValue", fifthInstruction.name);
		assertEquals("(Ljava/lang/Double;)J", fifthInstruction.desc);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.L2D, sixthInstruction.getOpcode());
	}

	@Test
	public void testFloatToNumber()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method()" +
				"{\n" +
					"return 1.23.floatValue();\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
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

		InvokeDynamicInsnNode fifthInstruction =
			(InvokeDynamicInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKEDYNAMIC, fifthInstruction.getOpcode());
		assertEquals("floatValue", fifthInstruction.name);
		assertEquals("(Ljava/lang/Double;)F", fifthInstruction.desc);

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.F2D, sixthInstruction.getOpcode());
	}

	@Test
	public void testCallPrivateMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"this.nothing();\n" +
				"}\n" +
				"private Void nothing() {}" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testFailToCallPrivateMethod()
	{
		String first =
			"package thingy;\n" +
			"class MyClass\n" +
			"{\n" +
				"private Void nothing() {}" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}";

		String second =
			"package thingy;\n" +
			"class NotherClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{" +
					"new MyClass().nothing();" +
				"}" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}";

		Compiler my = new Compiler("thingy/MyClass.java", first);
		Compiler nother = new Compiler("thingy/NotherClass.java", second);
		Classes classes = new Classes();

		my.compileClassDeclaration(classes);
		nother.compileClassDeclaration(classes);

		classes.clearCache();

		my.compileInterface(classes);
		nother.compileInterface(classes);

		classes.clearCache();

		CompilationResult result1 = my.compile(classes);
		CompilationResult result2 = nother.compile(classes);

		assertTrue(result1.isSuccessful());
		assertFalse(result2.isSuccessful());
	}

	@Test
	public void testVoidMethodCall()
	{
		String source =
			"import java.util.HashMap;\n" +
			"class MyClass\n" +
			"{\n" +
				"HashMap<String,String> map = new HashMap<String,String>();" +
				"public Void method()" +
				"{\n" +
					"this.map.clear();\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
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

		MethodNode method = classNode.methods.get(1);

		assertEquals(1, method.maxStack);
		assertEquals(1, method.maxLocals);

		InsnList instructions = method.instructions;

		assertEquals(8, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(label, thirdInstruction.start.getLabel());

		FieldInsnNode fourthInstruction = (FieldInsnNode) instructions.get(3);

		assertEquals(Opcodes.GETFIELD, fourthInstruction.getOpcode());

		LabelNode fifthInstruction = (LabelNode) instructions.get(4);
		Label notherLabel = fifthInstruction.getLabel();

		LineNumberNode sixthInstruction = (LineNumberNode) instructions.get(5);

		assertEquals(notherLabel, sixthInstruction.start.getLabel());

		InvokeDynamicInsnNode seventhInstruction =
			(InvokeDynamicInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKEDYNAMIC, seventhInstruction.getOpcode());

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.RETURN, eighthInstruction.getOpcode());
	}

	@Test
	public void testTypeConversionReturnsNumber()
	{
		String source =
			"import java.io.PrintStream;\n" +
			"class MyClass\n" +
			"{\n" +
				"PrintStream out = new System().getOut();\n" +
				"public Void method()\n" +
				"{\n" +
					"this.out.println(1.1.intValue());\n" +
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

		MethodNode method = classNode.methods.get(1);
		InsnList instructions = method.instructions;

		assertEquals(14, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(label, thirdInstruction.start.getLabel());

		FieldInsnNode fourthInstruction = (FieldInsnNode) instructions.get(3);

		assertEquals(Opcodes.GETFIELD, fourthInstruction.getOpcode());

		LdcInsnNode fifthInstruction = (LdcInsnNode) instructions.get(4);

		assertEquals(Opcodes.LDC, fifthInstruction.getOpcode());
		assertEquals(1.1, fifthInstruction.cst);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals("valueOf", sixthInstruction.name);

		LabelNode seventhInstruction = (LabelNode) instructions.get(6);
		Label notherLabel = seventhInstruction.getLabel();

		LineNumberNode eighthInstruction = (LineNumberNode) instructions.get(7);

		assertEquals(notherLabel, eighthInstruction.start.getLabel());

		InvokeDynamicInsnNode ninthInstruction =
			(InvokeDynamicInsnNode) instructions.get(8);

		assertEquals("intValue", ninthInstruction.name);

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.I2D, tenthInstruction.getOpcode());

		LabelNode eleventhInstruction = (LabelNode) instructions.get(10);
		Label ndAnotherLabel = eleventhInstruction.getLabel();

		LineNumberNode twelfthInstruction = (LineNumberNode) instructions.get(11);

		assertEquals(ndAnotherLabel, twelfthInstruction.start.getLabel());

		InvokeDynamicInsnNode thirteenthInstruction =
			(InvokeDynamicInsnNode) instructions.get(12);

		assertEquals(Opcodes.INVOKEDYNAMIC, thirteenthInstruction.getOpcode());
		assertEquals("println", thirteenthInstruction.name);
		assertEquals("(Ljava/io/PrintStream;D)V", thirteenthInstruction.desc);
	}

	@Test
	public void testRecursiveMethodCall()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method(Num number)" +
				"{\n" +
					"Num result = number + 1.0;" +
					"this.method(result);\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
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
		InsnList instructions = firstMethod.instructions;

		assertEquals(10, instructions.size());

		LabelNode firstInstruction = (LabelNode) instructions.get(0);

		Label label = firstInstruction.getLabel();

		assertTrue(instructions.get(1) instanceof FrameNode);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.DLOAD, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCONST_1, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DADD, fifthInstruction.getOpcode());

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.DSTORE, sixthInstruction.getOpcode());
		assertEquals(1, sixthInstruction.var);

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);

		assertEquals(Opcodes.GOTO, seventhInstruction.getOpcode());
		assertEquals(label, seventhInstruction.label.getLabel());
	}

	@Test
	public void testRecursiveMethodCallInsideBlock()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method(Num number)" +
				"{\n" +
					"Num result = number + 1.0;\n" +
					"if (true)\n" +
					"{\n" +
						"return this.method(result);\n" +
					"}\n" +
					"else\n" +
					"{\n" +
						"return result;\n" +
					"}\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
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
		InsnList instructions = firstMethod.instructions;

		LabelNode firstInstruction = (LabelNode) instructions.get(0);

		Label label = firstInstruction.getLabel();

		assertTrue(instructions.get(1) instanceof FrameNode);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.DLOAD, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCONST_1, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DADD, fifthInstruction.getOpcode());

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.DSTORE, sixthInstruction.getOpcode());
		assertEquals(3, sixthInstruction.var);

		InsnNode seventhInstruction = (InsnNode) instructions.get(6);

		assertEquals(Opcodes.ICONST_1, seventhInstruction.getOpcode());

		JumpInsnNode eighthInstruction = (JumpInsnNode) instructions.get(7);

		assertEquals(Opcodes.IFEQ, eighthInstruction.getOpcode());

		VarInsnNode ninthInstruction = (VarInsnNode) instructions.get(8);

		assertEquals(Opcodes.DLOAD, ninthInstruction.getOpcode());
		assertEquals(3, ninthInstruction.var);

		VarInsnNode tenthInstruction = (VarInsnNode) instructions.get(9);

		assertEquals(Opcodes.DSTORE, tenthInstruction.getOpcode());
		assertEquals(1, tenthInstruction.var);

		JumpInsnNode eleventhInstruction = (JumpInsnNode) instructions.get(10);

		assertEquals(Opcodes.GOTO, eleventhInstruction.getOpcode());
		assertEquals(label, eleventhInstruction.label.getLabel());
	}

	@Test
	public void testRecursiveMethodCallViaVariable()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num method(Num number)" +
				"{\n" +
					"Num result = this.method(number + 1.0);\n" +
					"return result;\n" +
				"}\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
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
		InsnList instructions = firstMethod.instructions;

		LabelNode firstInstruction = (LabelNode) instructions.get(0);

		Label label = firstInstruction.getLabel();

		assertTrue(instructions.get(1) instanceof FrameNode);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.DLOAD, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DCONST_1, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DADD, fifthInstruction.getOpcode());

		VarInsnNode sixthInstruction = (VarInsnNode) instructions.get(5);

		assertEquals(Opcodes.DSTORE, sixthInstruction.getOpcode());
		assertEquals(1, sixthInstruction.var);

		JumpInsnNode seventhInstruction = (JumpInsnNode) instructions.get(6);

		assertEquals(Opcodes.GOTO, seventhInstruction.getOpcode());
		assertEquals(label, seventhInstruction.label.getLabel());
	}

	@Test
	public void testMissingThis()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public String method(Num i)" +
				"{\n" +
					"String method = \"method\";" +
					"return method(i);\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 4:32 'method' is not a callable function.\n",
			result.getErrors());
	}

	@Test
	public void testCallingBrokenMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void getThingy()\n" +
				"{\n" +
					"this.getString();\n" +
				"}\n" +
				"public String getString()\n" +
				"{\n" +
					"return new Wibble();\n" +
				"}\n" +
			"}\n";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
	}

	@Test
	public void testCallingMethodWithNumberParameter()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"args.get(0.0);\n" +
				"}\n" +
			"}\n";

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
		InsnList instructions = firstMethod.instructions;

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

		VarInsnNode fourthInstruction = (VarInsnNode) instructions.get(3);

		assertEquals(Opcodes.ALOAD, fourthInstruction.getOpcode());
		assertEquals(1, fourthInstruction.var);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DCONST_0, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.D2I, sixthInstruction.getOpcode());
	}

	@Test
	public void testAmbiguousMethodCall()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"this.method(5);\n" +
				"}\n" +
				"public Void method(Object value) {}\n" +
				"public Void method(Num value) {}\n" +
			"}\n";

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
		InsnList instructions = firstMethod.instructions;

		InvokeDynamicInsnNode ninthInstruction =
			(InvokeDynamicInsnNode) instructions.get(8);

		assertEquals("(LMyClass;D)V", ninthInstruction.desc);
	}

	@Test
	public void testVariableArguments()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"new Array<Num>(1, 3, 5.8);\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
	}

	@Test
	@Ignore
	public void testCreatingSet()
	{
		String source =
			"import java.util.HashSet;\n" +
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"new HashSet<String>(new Array<String>());\n" +
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

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/util/HashSet", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		TypeInsnNode thirdInstruction = (TypeInsnNode) instructions.get(2);

		assertEquals(Opcodes.NEW, thirdInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Array", thirdInstruction.desc);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DUP, fourthInstruction.getOpcode());

		assertTrue(instructions.get(4) instanceof LabelNode);
		assertTrue(instructions.get(5) instanceof LineNumberNode);

		MethodInsnNode seventhInstruction = (MethodInsnNode) instructions.get(6);

		assertEquals(Opcodes.INVOKESPECIAL, seventhInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Array", seventhInstruction.owner);
		assertEquals("<init>", seventhInstruction.name);
		assertEquals("()V", seventhInstruction.desc);

		MethodInsnNode eighthInstruction = (MethodInsnNode) instructions.get(7);

		assertEquals(Opcodes.INVOKEVIRTUAL, eighthInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/Array", eighthInstruction.owner);
		assertEquals("toArray", eighthInstruction.name);
		assertEquals("()[Ljava/lang/Object;", eighthInstruction.desc);

		MethodInsnNode ninthInstruction = (MethodInsnNode) instructions.get(8);

		assertEquals(Opcodes.INVOKESTATIC, ninthInstruction.getOpcode());
		assertEquals("java/util/Arrays", ninthInstruction.owner);
		assertEquals("asList", ninthInstruction.name);
		assertEquals("([Ljava/lang/Object;)Ljava/util/List;", ninthInstruction.desc);
	}
}
