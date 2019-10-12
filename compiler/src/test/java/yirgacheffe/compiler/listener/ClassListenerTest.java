package yirgacheffe.compiler.listener;

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClassListenerTest
{
	@Test
	public void testParseError()
	{
		String source = "interface MyInterface {{";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(1, result.getErrors().split("\n").length);
		assertEquals("line 1:23 mismatched input", result.getErrors().substring(0, 26));
	}

	@Test
	public void testNamedEmptyInterface()
	{
		String source = "interface MyInterface {}";
		Compiler compiler = new Compiler("MyInterface.yg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
		assertEquals("MyInterface.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;

		assertEquals("MyInterface.yg", classNode.sourceFile);
		assertEquals("MyInterface", classNode.name);
		assertEquals(access, classNode.access);
		assertEquals(0, classNode.fields.size());
	}

	@Test
	public void testMultipleClassDeclarations()
	{
		String source = "interface MyInterface {} class MyClass { public MyClass() {} }";
		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 File contains multiple class declarations.\n",
			result.getErrors());
	}

	@Test
	public void testFailToDeclareClassOrInterface()
	{
		String source = "thingy MyInterface {}";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Expected declaration of class or interface.\n" +
			"line 1:0 Class has no constructor.\n",
			result.getErrors());
	}

	@Test
	public void testNamedEmptyClass()
	{
		String source = "class MyClass { public MyClass() {} }";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
		assertEquals("MyClass.class", result.getClassFileName());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals("MyClass", classNode.name);
		assertEquals(0, classNode.fields.size());
		assertEquals(
			Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SUPER,
			classNode.access);
	}

	@Test
	public void testMissingConstructor()
	{
		String source = "class MyClass {}";
		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 1:0 Class has no constructor.\n", result.getErrors());
	}

	@Test
	public void testInterfaceWithMissingIdentifier()
	{
		String source =
			"interface\n" +
			"{\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Interface identifier expected.\n",
			result.getErrors());
	}

	@Test
	public void testClassWithMissingIdentifier()
	{
		String source =
			"class\n" +
			"{\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Class identifier expected.\n" +
			"line 1:0 Class has no constructor.\n",
			result.getErrors());
	}

	@Ignore
	@Test
	public void testClassWithMissingCloseBlock()
	{
		String source =
			"class MyClass\n" +
			"{";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 2:1 Missing '}'.\n",
			result.getErrors());
	}

	@Test
	public void testClassInPackage()
	{
		String source = "package myPackage; class MyClass { public MyClass() {} }";
		Compiler compiler = new Compiler("myPackage/gile.gg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
		assertEquals("myPackage/MyClass.class", result.getClassFileName());
	}

	@Test
	public void testClassInNestedPackage()
	{
		String source = "package myPackage.thingy; class MyClass { public MyClass() {} }";
		Compiler compiler = new Compiler("myPackage/thingy/file.yg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());
		assertEquals("myPackage/thingy/MyClass.class", result.getClassFileName());
	}

	@Test
	public void testClassWithMissingPackage()
	{
		String source = "class MyClass { public MyClass() {} }";
		Compiler compiler = new Compiler("anotherPackage/wibble/file.yg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Missing package declaration " +
				"for file path anotherPackage/wibble/.\n",
			result.getErrors());
	}

	@Test
	public void testClassInPackageWrongPackage()
	{
		String source = "package myPackage.wibble; class MyClass { public MyClass() {} }";
		Compiler compiler = new Compiler("anotherPackage/wibble/file.yg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:8 Package name myPackage.wibble " +
				"does not correspond to the file path anotherPackage/wibble/.\n",
			result.getErrors());
	}

	@Test
	public void testPackagedClass()
	{
		Classes classes = new Classes();
		String source = "package tis.that; interface MyInterface {}";
		Compiler compiler = new Compiler("tis/that/MyInterface.yg", source);
		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		source =
			"package tis.that;\n" +
			"interface AnotherInterface\n" +
			"{\n" +
				"MyInterface myMethod();\n" +
			"}";

		compiler = new Compiler("tis/that/AnotherInterface.yg", source);
		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode method = classNode.methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals("()Ltis/that/MyInterface;", method.desc);
	}

	@Test
	public void testClassWithInterfaceMethod()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String myMethod();\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals("line 3:0 Method requires method body.\n", result.getErrors());
	}

	@Test
	public void testInterfaceWithClassMethod()
	{
		String source =
			"interface MyInterface\n" +
			"{\n" +
				"public String myMethod()" +
				"{" +
					"return \"\";" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Method body not permitted for interface method.\n",
			result.getErrors());
	}

	@Test
	public void testImportUnknownType()
	{
		String source =
			"import java.util.Liszt;\n" +
			"class MyClass { public MyClass() {} }";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:7 Unrecognised type: java.util.Liszt is not a type.\n",
			result.getErrors());
	}

	@Test
	public void testWrongPackage()
	{
		String source =
			"package yirgacheffe;\n" +
			"class MyClass\n" +
			"{\n" +
				"public MyClass() {}\n" +
				"public MyClass method()\n" +
				"{\n" +
					"return this.method().method();\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:8 Package name yirgacheffe does not correspond to the file path .\n" +
			"line 5:7 Unrecognised type: MyClass is not a type.\n" +
			"line 7:11 Method java.lang.Object.method() not found.\n" +
			"line 7:20 Method java.lang.Object.method() not found.\n",
			result.getErrors());
	}

	@Test
	public void testEnumeration()
	{
		String source =
			"enumeration MyNumeration of String\n" +
			"{\n" +
				"Num number;\n" +
				"\"ONE\":(1);\n" +
				"\"TWO\":(2);\n" +
				"MyNumeration(Num number)\n" +
				"{\n" +
					"this.number = number;\n" +
				"}\n" +
				"public String myMethod()\n" +
				"{\n" +
					"return \"\";\n" +
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

		assertEquals(Arrays.asList("yirgacheffe/lang/Enumeration"), classNode.interfaces);

		FieldNode valuesField = classNode.fields.get(0);

		assertEquals("values", valuesField.name);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, valuesField.access);
		assertEquals("Ljava/util/Map;", valuesField.desc);

		assertEquals(6, classNode.methods.size());

		this.checkEnumerationInitialiser(classNode.methods.get(5));

		MethodNode method = classNode.methods.get(0);

		assertEquals("ONE", method.name);
		assertEquals(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, method.access);
		assertEquals("()LMyNumeration;", method.desc);

		InsnList instructions = method.instructions;

		assertEquals(5, instructions.size());

		FieldInsnNode firstInstruction = (FieldInsnNode) instructions.get(0);

		assertEquals(Opcodes.GETSTATIC, firstInstruction.getOpcode());
		assertEquals("MyNumeration", firstInstruction.owner);
		assertEquals("values", firstInstruction.name);
		assertEquals("Ljava/util/Map;", firstInstruction.desc);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(Opcodes.LDC, secondInstruction.getOpcode());
		assertEquals("ONE", secondInstruction.cst);

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKEINTERFACE, thirdInstruction.getOpcode());
		assertEquals("java/util/Map", thirdInstruction.owner);
		assertEquals("get", thirdInstruction.name);
		assertEquals("(Ljava/lang/Object;)Ljava/lang/Object;", thirdInstruction.desc);
	}

	private void checkEnumerationInitialiser(MethodNode initialiser)
	{
		assertEquals("<clinit>", initialiser.name);
		assertEquals(Opcodes.ACC_STATIC, initialiser.access);
		assertEquals("()V", initialiser.desc);

		InsnList instructions = initialiser.instructions;

		assertEquals(25, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("java/util/HashMap", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		MethodInsnNode thirdInstruction = (MethodInsnNode) instructions.get(2);

		assertEquals(Opcodes.INVOKESPECIAL, thirdInstruction.getOpcode());
		assertEquals("java/util/HashMap", thirdInstruction.owner);
		assertEquals("<init>", thirdInstruction.name);
		assertEquals("()V", thirdInstruction.desc);

		FieldInsnNode fourthInstruction = (FieldInsnNode) instructions.get(3);

		assertEquals(Opcodes.PUTSTATIC, fourthInstruction.getOpcode());
		assertEquals("MyNumeration", fourthInstruction.owner);
		assertEquals("values", fourthInstruction.name);
		assertEquals("Ljava/util/Map;", fourthInstruction.desc);

		FieldInsnNode fifthInstruction = (FieldInsnNode) instructions.get(4);

		assertEquals(Opcodes.GETSTATIC, fifthInstruction.getOpcode());
		assertEquals("MyNumeration", fifthInstruction.owner);
		assertEquals("values", fifthInstruction.name);
		assertEquals("Ljava/util/Map;", fifthInstruction.desc);

		LdcInsnNode sixthInstruction = (LdcInsnNode) instructions.get(5);

		assertEquals(Opcodes.LDC, sixthInstruction.getOpcode());
		assertEquals("ONE", sixthInstruction.cst);

		TypeInsnNode seventhInstruction = (TypeInsnNode) instructions.get(6);

		assertEquals(Opcodes.NEW, seventhInstruction.getOpcode());
		assertEquals("MyNumeration", seventhInstruction.desc);

		InsnNode eighthInstruction = (InsnNode) instructions.get(7);

		assertEquals(Opcodes.DUP, eighthInstruction.getOpcode());

		InsnNode ninthInstruction = (InsnNode) instructions.get(8);

		assertEquals(Opcodes.ICONST_1, ninthInstruction.getOpcode());

		InsnNode tenthInstruction = (InsnNode) instructions.get(9);

		assertEquals(Opcodes.I2D, tenthInstruction.getOpcode());

		assertTrue(instructions.get(10) instanceof LabelNode);
		assertTrue(instructions.get(11) instanceof LineNumberNode);

		MethodInsnNode thirteenthInstruction = (MethodInsnNode) instructions.get(12);

		assertEquals(Opcodes.INVOKESPECIAL, thirteenthInstruction.getOpcode());
		assertEquals("<init>", thirteenthInstruction.name);

		MethodInsnNode fourteenthInstruction = (MethodInsnNode) instructions.get(13);

		assertEquals(Opcodes.INVOKEINTERFACE, fourteenthInstruction.getOpcode());
		assertEquals("java/util/Map", fourteenthInstruction.owner);
		assertEquals("put", fourteenthInstruction.name);

		assertEquals(
			"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
			fourteenthInstruction.desc);
	}

	@Test
	public void testEnumerationConstructorCannotBePublic()
	{
		String source =
			"enumeration MyNumeration of String\n" +
			"{\n" +
				"public MyNumeration()\n" +
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
			"line 3:0 Enumeration constructor cannot be public.\n",
			result.getErrors());
	}

	@Test
	public void testEnumerationFromUnknownVariable()
	{
		String source =
			"enumeration MyNumeration of String\n" +
			"{\n" +
				"Num number;\n" +
				"\"ONE\":(1);\n" +
				"\"TWO\":(thingy);\n" +
				"MyNumeration(Num number)\n" +
				"{\n" +
					"this.number = number;\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals("line 5:7 Unknown local variable 'thingy'.\n", result.getErrors());
	}

	@Test
	public void testEnumerationFromArgumentOfWrongType()
	{
		String source =
			"enumeration MyNumeration of String\n" +
			"{\n" +
				"Num number;\n" +
				"\"ONE\":(1);\n" +
				"\"TWO\":(\"\");\n" +
				"MyNumeration(Num number)\n" +
				"{\n" +
					"this.number = number;\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());

		assertEquals(
			"line 5:0 Constructor MyNumeration(java.lang.String) not found.\n",
			result.getErrors());
	}

	@Test
	public void testEnumerationFromConstantOfWrongType()
	{
		String source =
			"enumeration MyNumeration of String\n" +
			"{\n" +
				"1:();\n" +
				"MyNumeration()\n" +
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
			"line 3:0 Enumeration constant 1 is not of type java.lang.String.\n",
			result.getErrors());
	}

	@Test
	public void testEnumerationOfNonEnumerableType()
	{
		String source =
			"enumeration MyNumeration of Object\n" +
			"{\n" +
				"MyNumeration()\n" +
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
			"line 1:28 Cannot enumerate type java.lang.Object.\n",
			result.getErrors());
	}

	@Test
	public void testEnumerationWithDefualt()
	{
		String source =
			"enumeration MyNumeration of String\n" +
			"{\n" +
				"MyNumeration()\n" +
				"{\n" +
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

		assertEquals("MyNumeration", classNode.name);

		MethodNode initialiser = classNode.methods.get(2);

		assertEquals(
			Arrays.asList("yirgacheffe/lang/EnumerationWithDefault"),
			classNode.interfaces);
		assertEquals("<clinit>", initialiser.name);
		assertEquals(Opcodes.ACC_STATIC, initialiser.access);
		assertEquals("()V", initialiser.desc);

		InsnList instructions = initialiser.instructions;

		assertEquals(8, instructions.size());

		TypeInsnNode firstInstruction = (TypeInsnNode) instructions.get(0);

		assertEquals(Opcodes.NEW, firstInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/DefaultingHashMap", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DUP, secondInstruction.getOpcode());

		TypeInsnNode thirdInstruction = (TypeInsnNode) instructions.get(2);

		assertEquals(Opcodes.NEW, thirdInstruction.getOpcode());
		assertEquals("MyNumeration", thirdInstruction.desc);

		InsnNode fourthInstruction = (InsnNode) instructions.get(3);

		assertEquals(Opcodes.DUP, fourthInstruction.getOpcode());

		MethodInsnNode fifthInstruction = (MethodInsnNode) instructions.get(4);

		assertEquals(Opcodes.INVOKESPECIAL, fifthInstruction.getOpcode());
		assertEquals("MyNumeration", fifthInstruction.owner);
		assertEquals("<init>", fifthInstruction.name);
		assertEquals("()V", fifthInstruction.desc);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKESPECIAL, sixthInstruction.getOpcode());
		assertEquals("yirgacheffe/lang/DefaultingHashMap", sixthInstruction.owner);
		assertEquals("<init>", sixthInstruction.name);
		assertEquals("(Ljava/lang/Object;)V", sixthInstruction.desc);

		FieldInsnNode seventhInstruction = (FieldInsnNode) instructions.get(6);

		assertEquals(Opcodes.PUTSTATIC, seventhInstruction.getOpcode());
		assertEquals("MyNumeration", seventhInstruction.owner);
		assertEquals("values", seventhInstruction.name);
		assertEquals("Ljava/util/Map;", seventhInstruction.desc);
	}
}
