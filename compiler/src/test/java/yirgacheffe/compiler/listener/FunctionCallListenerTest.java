package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import java.util.List;

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

		assertEquals(5, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/String", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESPECIAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/String", thirdInstruction.owner);
		assertEquals("<init>", thirdInstruction.name);
		assertEquals("()V", thirdInstruction.desc);
		assertFalse(thirdInstruction.itf);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.POP, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.RETURN, fifthInstruction.getOpcode());
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

		assertEquals(6, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/String", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		LdcInsnNode thirdInstruction = (LdcInsnNode) instructions.get(2);

		assertEquals(Opcodes.LDC, thirdInstruction.getOpcode());
		assertEquals("thingy", thirdInstruction.cst);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESPECIAL, fourthInstruction.getOpcode());
		assertEquals("java/lang/String", fourthInstruction.owner);
		assertEquals("<init>", fourthInstruction.name);
		assertEquals("(Ljava/lang/String;)V", fourthInstruction.desc);
		assertFalse(fourthInstruction.itf);

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.POP, fifthInstruction.getOpcode());

		InsnNode sixthInstruction = (InsnNode) instructions.get(5);

		assertEquals(Opcodes.RETURN, sixthInstruction.getOpcode());
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

		assertEquals(6, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/lang/Double", firstInstruction.desc);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.DCONST_1, thirdInstruction.getOpcode());

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESPECIAL, fourthInstruction.getOpcode());
		assertEquals("java/lang/Double", fourthInstruction.owner);
		assertEquals("<init>", fourthInstruction.name);
		assertEquals("(D)V", fourthInstruction.desc);
		assertFalse(fourthInstruction.itf);
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
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		assertEquals(1, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		assertEquals(4, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKEVIRTUAL, secondInstruction.getOpcode());
		assertEquals("java/lang/String", secondInstruction.owner);
		assertEquals("toString", secondInstruction.name);
		assertEquals("()Ljava/lang/String;", secondInstruction.desc);
		assertFalse(secondInstruction.itf);

		InsnNode thirdInstruction = (InsnNode) instructions.get(2);

		assertEquals(Opcodes.POP, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.RETURN, fourthInstruction.getOpcode());
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

		assertEquals(5, instructions.size());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/String", thirdInstruction.owner);
		assertEquals("(Ljava/lang/Object;)Z", thirdInstruction.desc);
		assertEquals("equals", thirdInstruction.name);
		assertFalse(thirdInstruction.itf);
	}

	@Test
	public void testConstructorCallWithWrongArgument()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Object method()" +
				"{\n" +
					"return new String(1);\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:7 Constructor java.lang.String(Num) not found.\n",
			result.getErrors());
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

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKESPECIAL, fourthInstruction.getOpcode());
		assertEquals("java/lang/ref/WeakReference", fourthInstruction.owner);
		assertEquals("(Ljava/lang/Object;)V", fourthInstruction.desc);
		assertEquals("<init>", fourthInstruction.name);
		assertFalse(fourthInstruction.itf);
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
	public void testMethodCallWithMismatchedTypeParameter()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()" +
				"{\n" +
					"MutableReference<String> ref = " +
						"new MutableReference<String>(\"thingy\");\n" +
					"ref.set(new Object());" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:3 Argument of type java.lang.Object cannot be assigned to " +
				"generic parameter of type java.lang.String.\n",
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
	public void testFunctionCallWithArgumentOfWrongType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"\"thingy\".split(false);\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:9 Method java.lang.String.split(Bool) not found.\n",
			result.getErrors());
	}

	@Test
	public void testUndefinedFunctionCall()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"\"thingy\".notAMethod();\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:9 Method java.lang.String.notAMethod() not found.\n",
			result.getErrors());
	}

	@Test
	public void testUndefinedFunctionCallOnNumber()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"123.notAMethod();\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:4 Method Num.notAMethod() not found.\n",
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
					"123.toString();\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		assertEquals(2, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		assertEquals(5, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(123.0, firstInstruction.cst);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("java/lang/Double", secondInstruction.owner);
		assertEquals("valueOf", secondInstruction.name);
		assertEquals("(D)Ljava/lang/Double;", secondInstruction.desc);
		assertFalse(secondInstruction.itf);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/Double", thirdInstruction.owner);
		assertEquals("toString", thirdInstruction.name);
		assertEquals("()Ljava/lang/String;", thirdInstruction.desc);
		assertFalse(thirdInstruction.itf);
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
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		assertEquals(1, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		assertEquals(5, instructions.size());

		InsnNode firstInstruction = (InsnNode) instructions.get(0);

		assertEquals(Opcodes.ICONST_1, firstInstruction.getOpcode());

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("java/lang/Boolean", secondInstruction.owner);
		assertEquals("valueOf", secondInstruction.name);
		assertEquals("(Z)Ljava/lang/Boolean;", secondInstruction.desc);
		assertFalse(secondInstruction.itf);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/Boolean", thirdInstruction.owner);
		assertEquals("toString", thirdInstruction.name);
		assertEquals("()Ljava/lang/String;", thirdInstruction.desc);
		assertFalse(thirdInstruction.itf);
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

		assertEquals(5, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(1.23, firstInstruction.cst);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESTATIC, secondInstruction.getOpcode());
		assertEquals("java/lang/Double", secondInstruction.owner);
		assertEquals("valueOf", secondInstruction.name);
		assertEquals("(D)Ljava/lang/Double;", secondInstruction.desc);
		assertFalse(secondInstruction.itf);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/Double", thirdInstruction.owner);
		assertEquals("intValue", thirdInstruction.name);
		assertEquals("()I", thirdInstruction.desc);
		assertFalse(thirdInstruction.itf);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.I2D, fourthInstruction.getOpcode());

		InsnNode fifthInstruction = (InsnNode) instructions.get(4);

		assertEquals(Opcodes.DRETURN, fifthInstruction.getOpcode());
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
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;
		MethodNode firstMethod = (MethodNode) methods.get(0);

		assertEquals(2, firstMethod.maxStack);
		assertEquals(1, firstMethod.maxLocals);

		InsnList instructions = firstMethod.instructions;

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/Double", thirdInstruction.owner);
		assertEquals("longValue", thirdInstruction.name);
		assertEquals("()J", thirdInstruction.desc);
		assertFalse(thirdInstruction.itf);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.L2D, fourthInstruction.getOpcode());
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

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());
		assertEquals("java/lang/Double", thirdInstruction.owner);
		assertEquals("floatValue", thirdInstruction.name);
		assertEquals("()F", thirdInstruction.desc);
		assertFalse(thirdInstruction.itf);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.F2D, fourthInstruction.getOpcode());
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
			"}";

		String second =
			"package thingy;\n" +
			"class NotherClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{" +
					"new MyClass().nothing();" +
				"}" +
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
	public void testAmbiguousFunctionCall()
	{
		String source =
			"import java.util.HashMap;" +
			"import java.util.Map;" +
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"this.mapIt(new HashMap<String,String>());\n" +
				"}\n" +
				"public Void mapIt(Map<String,String> map) {}\n" +
				"public Void mapIt(Object map) {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:5 Ambiguous call to method MyClass.mapIt.\n",
			result.getErrors());
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

		List methods = classNode.methods;
		MethodNode method = (MethodNode) methods.get(1);

		assertEquals(1, method.maxStack);
		assertEquals(1, method.maxLocals);

		InsnList instructions = method.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		FieldInsnNode secondInstruction = (FieldInsnNode) instructions.get(1);

		assertEquals(Opcodes.GETFIELD, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEVIRTUAL, thirdInstruction.getOpcode());

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.RETURN, fourthInstruction.getOpcode());
	}
}
