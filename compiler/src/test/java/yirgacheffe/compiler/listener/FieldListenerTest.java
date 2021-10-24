package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
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
import org.objectweb.asm.tree.VarInsnNode;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldListenerTest
{
	@Test
	public void testClassWithNumberField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Num myField;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = (FieldNode) fields.get(0);

		assertEquals(Opcodes.ACC_PROTECTED, firstField.access);
		assertEquals("D", firstField.desc);
		assertEquals("myField", firstField.name);
	}

	@Test
	public void testClassWithStringField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String myStringField;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = (FieldNode) fields.get(0);

		assertEquals(Opcodes.ACC_PROTECTED, firstField.access);
		assertEquals("Ljava/lang/String;", firstField.desc);
		assertEquals("myStringField", firstField.name);
	}

	@Test
	public void testClassWithNumberAndStringFields()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Num myNumberField;\n" +
				"String myStringField;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List fields = classNode.fields;

		assertEquals(2, fields.size());

		FieldNode firstField = (FieldNode) fields.get(0);

		assertEquals("D", firstField.desc);
		assertEquals("myNumberField", firstField.name);

		FieldNode secondField = (FieldNode) fields.get(1);

		assertEquals("Ljava/lang/String;", secondField.desc);
		assertEquals("myStringField", secondField.name);
	}

	@Test
	public void testInterfaceWithField()
	{
		String source =
			"interface MyClass\n" +
			"{\n" +
				"Num myField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Interface cannot contain field.\n",
			result.getErrors());
	}

	@Test
	public void testClassFieldWithMissingType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				" myField;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:1 Field declaration should start with type.\n",
			result.getErrors());
	}

	@Test
	public void testDuplicateField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String myField;\n" +
				"String myField = \"thingy\";\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Duplicate field 'myField'.\n",
			result.getErrors());
	}

	@Test
	public void testDuplicateInitialisedFields()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String myField = \"thingy\";\n" +
				"String myField = \"thingy\";\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Duplicate field 'myField'.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithUnknownType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Thingy myStringField;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Unrecognised type: Thingy is not a type.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithFullyQualifiedType()
	{
		String source =
			"import java.util.List;\n" +
			"class MyClass\n" +
			"{\n" +
				"java.util.List<String> myListField;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List fields = classNode.fields;
		String descriptor = ((FieldNode) fields.get(0)).desc;

		assertEquals("Ljava/util/List;", descriptor);
	}

	@Test
	public void testFieldWithUnknownFullyQualifiedType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"java.thingy.List myListField;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Unrecognised type: java.thingy.List is not a type.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithImportedType()
	{
		String source =
			"import java.util.List;\n" +
			"class MyClass\n" +
			"{\n" +
				"List<String> myListField;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List fields = classNode.fields;
		String descriptor = ((FieldNode) fields.get(0)).desc;

		assertEquals("Ljava/util/List;", descriptor);
	}

	@Test
	public void testStringFieldWithInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String myStringField = \"thingy\";\n" +
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

		assertEquals(3, classNode.methods.size());

		MethodNode initialiser = classNode.methods.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, initialiser.access);
		assertEquals("0init_field_myStringField", initialiser.name);
		assertEquals("()V", initialiser.desc);
		assertEquals(1, initialiser.maxLocals);
		assertEquals(2, initialiser.maxStack);

		InsnList instructions = initialiser.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals("thingy", secondInstruction.cst);

		FieldInsnNode thirdInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.PUTFIELD, thirdInstruction.getOpcode());
		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("myStringField", thirdInstruction.name);
		assertEquals("Ljava/lang/String;", thirdInstruction.desc);

		assertEquals(Opcodes.RETURN, instructions.get(3).getOpcode());

		MethodNode constructor = classNode.methods.get(1);

		assertEquals("<init>", constructor.name);

		instructions = constructor.instructions;

		VarInsnNode thirdInsn = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInsn.getOpcode());
		assertEquals(0, thirdInsn.var);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("0init_field_myStringField", fourthInstruction.name);
		assertEquals("()V", fourthInstruction.desc);
		assertEquals("MyClass", fourthInstruction.owner);
		assertFalse(fourthInstruction.itf);
	}

	@Test
	public void testNumberFieldWithIntegerInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Num myNumberField = 5.0;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode initialiser = classNode.methods.get(0);
		InsnList instructions = initialiser.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(5.0, secondInstruction.cst);

		FieldInsnNode thirdInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.PUTFIELD, thirdInstruction.getOpcode());
		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("myNumberField", thirdInstruction.name);
		assertEquals("D", thirdInstruction.desc);
	}

	@Test
	public void testNumberFieldWithDecimalInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Num myNumberField = 1.2;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode initialiser = classNode.methods.get(0);
		InsnList instructions = initialiser.instructions;

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(1.2, secondInstruction.cst);
	}

	@Test
	public void testCharacterFieldWithInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Char myCharacterField = 'a';\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode initialiser = classNode.methods.get(0);
		InsnList instructions = initialiser.instructions;

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals((int) 'a', secondInstruction.cst);

		FieldInsnNode thirdInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.PUTFIELD, thirdInstruction.getOpcode());
		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("myCharacterField", thirdInstruction.name);
		assertEquals("C", thirdInstruction.desc);
	}

	@Test
	public void testBooleanFieldWithInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Bool myBooleanField = true;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		MethodNode initialiser = classNode.methods.get(0);
		InsnList instructions = initialiser.instructions;

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ICONST_1, secondInstruction.getOpcode());

		FieldInsnNode thirdInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.PUTFIELD, thirdInstruction.getOpcode());
		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("myBooleanField", thirdInstruction.name);
		assertEquals("Z", thirdInstruction.desc);
	}

	@Test
	public void testObjectFieldWithInitialiser()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Object myObject = new Object();\n" +
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

		List methods = classNode.methods;

		assertEquals(3, methods.size());

		MethodNode initialiser = classNode.methods.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, initialiser.access);
		assertEquals("0init_field_myObject", initialiser.name);
		assertEquals("()V", initialiser.desc);
		assertEquals(1, initialiser.maxLocals);
		assertEquals(3, initialiser.maxStack);

		InsnList instructions = initialiser.instructions;

		FieldInsnNode seventhInstruction = (FieldInsnNode) instructions.get(6);

		assertEquals(Opcodes.PUTFIELD, seventhInstruction.getOpcode());
		assertEquals("MyClass", seventhInstruction.owner);
		assertEquals("myObject", seventhInstruction.name);
		assertEquals("Ljava/lang/Object;", seventhInstruction.desc);

		MethodNode constructor = classNode.methods.get(1);
		instructions = constructor.instructions;

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("0init_field_myObject", fourthInstruction.name);
		assertEquals("MyClass", fourthInstruction.owner);
		assertEquals("()V", fourthInstruction.desc);
	}

	@Test
	public void testTwoInitialisedFields()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Object myObject = new Object();\n" +
				"String myString = \"thingy\";\n" +
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

		List<MethodNode> methods = classNode.methods;

		assertEquals(4, methods.size());

		MethodNode first = classNode.methods.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, first.access);
		assertEquals("0init_field_myObject", first.name);

		MethodNode second = classNode.methods.get(1);

		assertEquals(Opcodes.ACC_PRIVATE, second.access);
		assertEquals("0init_field_myString", second.name);

		MethodNode constructor = classNode.methods.get(2);

		InsnList instructions = constructor.instructions;

		assertEquals(9, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		MethodInsnNode secondInstruction = (MethodInsnNode) instructions.get(1);

		assertEquals(Opcodes.INVOKESPECIAL, secondInstruction.getOpcode());
		assertEquals("<init>", secondInstruction.name);
		assertEquals("java/lang/Object", secondInstruction.owner);
		assertEquals("()V", secondInstruction.desc);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("0init_field_myString", fourthInstruction.name);
		assertEquals("MyClass", fourthInstruction.owner);
		assertEquals("()V", fourthInstruction.desc);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ALOAD, fifthInstruction.getOpcode());
		assertEquals(0, fifthInstruction.var);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKEVIRTUAL, sixthInstruction.getOpcode());
		assertEquals("0init_field_myObject", sixthInstruction.name);
		assertEquals("MyClass", sixthInstruction.owner);
		assertEquals("()V", sixthInstruction.desc);
	}

	@Test
	public void testFieldInitialiserWithMismatchedTypes()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Num myBooleanField = true;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Cannot assign Bool to field of type Num.\n",
			result.getErrors());
	}

	@Test
	public void testPublicField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Num myField;\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Field cannot be declared with access modifier.\n",
			result.getErrors());
	}

	@Test
	public void testReadFromStringField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String myStringField = \"thingy\";\n" +
				"public Void read()\n" +
				"{\n" +
					"String read = this.myStringField;\n" +
				"}" +
				"public MyClass() {}\n" +
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

		List fields = classNode.fields;

		assertEquals(1, fields.size());

		assertEquals(4, classNode.methods.size());

		MethodNode method = classNode.methods.get(1);
		InsnList instructions = method.instructions;

		assertEquals(6, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		LabelNode secondInstruction = (LabelNode) instructions.get(1);
		Label label = secondInstruction.getLabel();

		LineNumberNode thirdInstruction = (LineNumberNode) instructions.get(2);

		assertEquals(label, thirdInstruction.start.getLabel());

		FieldInsnNode fourthInstruction = (FieldInsnNode) instructions.get(3);

		assertEquals(Opcodes.GETFIELD, fourthInstruction.getOpcode());
		assertEquals("MyClass", fourthInstruction.owner);
		assertEquals("myStringField", fourthInstruction.name);
		assertEquals("Ljava/lang/String;", fourthInstruction.desc);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ASTORE, fifthInstruction.getOpcode());
		assertEquals(1, fifthInstruction.var);
	}

	@Test
	public void testReadFromUnknownField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void read()\n" +
				"{\n" +
					"String read = this.myStringField;\n" +
				"}" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 5:18 Unknown field 'myStringField'.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWriteOutsideOfConstructor()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy = \"thingy\";\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"this.thingy = \"sumpt\";\n" +
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
			"line 6:0 Fields must be assigned in initialisers or constructors.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWriteInInvalidConstructor()
	{
		String source =
			"class MyClass\n" +
			"{" +
				"String myString;\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
				"public method()\n" +
				"{\n" +
					"this.myString = \"thingy\";\n" +
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
	}

	@Test
	public void testFieldWriteInInvalidMethod()
	{
		String source =
			"class MyClass\n" +
			"{" +
				"String myString;\n" +
				"public Void method()\n" +
				"{\n" +
				"}\n" +
				"public Void method()\n" +
				"{\n" +
					"this.myString = \"thingy\";\n" +
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
	}

	@Test
	public void testAssignFieldInConstructor()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy;\n" +
				"public MyClass()\n" +
				"{\n" +
					"this.thingy = \"sumpt\";\n" +
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

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List fields = classNode.fields;

		assertEquals(1, fields.size());
	}

	@Test
	public void testAssignWrongTypeToField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy = \"thingy\";\n" +
				"public MyClass()\n" +
				"{\n" +
					"this.thingy = 1;\n" +
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
			"line 6:0 Cannot assign expression of type Num " +
			"to field of type java.lang.String.\n",
			result.getErrors());
	}

	@Test
	public void testCallMethodOnUnknownField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public Void method()\n" +
				"{\n" +
					"Num number = 1;\n" +
					"out.println(1);\n" +
				"}\n" +
				"public MyClass() {}\n" +
			"}";

		Compiler compiler = new Compiler("", source);

		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 6:0 Unknown local variable 'out'.\n" +
			"line 6:3 Invoked method java.lang.Object.println(Num) not found.\n",
			result.getErrors());
	}

	@Test
	public void testConstantField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"const String myField = \"thingy\";\n" +
				"public MyClass() {}\n" +
				"public String method()\n" +
				"{\n" +
					"return myField;\n" +
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

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List fields = classNode.fields;

		assertEquals(0, fields.size());
		assertEquals(3, classNode.methods.size());

		MethodNode constructor = classNode.methods.get(0);

		assertEquals(5, constructor.instructions.size());

		MethodNode method = classNode.methods.get(2);

		assertEquals("method", method.name);

		InsnList instructions = method.instructions;

		assertEquals(2, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals("thingy", firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.ARETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testConstantNumberField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"const Num myField = 1;\n" +
				"public Num method()\n" +
				"{\n" +
					"return myField;\n" +
				"}\n" +
				"public MyClass() {}\n" +
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

		List fields = classNode.fields;

		assertEquals(0, fields.size());

		MethodNode method = classNode.methods.get(0);

		assertEquals("method", method.name);

		InsnList instructions = method.instructions;

		assertEquals(2, instructions.size());

		LdcInsnNode firstInstruction = (LdcInsnNode) instructions.get(0);

		assertEquals(Opcodes.LDC, firstInstruction.getOpcode());
		assertEquals(1, firstInstruction.cst);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.DRETURN, secondInstruction.getOpcode());
	}

	@Test
	public void testCantAssignToConstantField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"const String myField = \"thingy\";\n" +
				"public MyClass()\n" +
				"{\n" +
					"myField = \"sumpt\";\n" +
					"this.myField = \"sumpt\";\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 6:0 Assignment to uninitialised variable 'myField'.\n" +
			"line 7:0 Assignment to unknown field 'myField'.\n",
			result.getErrors());
	}

	@Test
	public void testMissingValueOfConstantField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"const String myField;\n" +
				"public MyClass()\n" +
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
			"line 3:0 Missing value of constant 'myField'.\n",
			result.getErrors());
	}

	@Test
	public void testConstantFieldDoesntBelongToThis()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"const String myField = \"thingy\";\n" +
				"public MyClass() {}\n" +
				"public String method()" +
				"{\n" +
					"return this.myField;\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 6:11 Unknown field 'myField'.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithUnknownPrimaryType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Wibble<String> ref;\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}\n";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Unrecognised type: Wibble is not a type.\n" +
			"line 3:0 Type Wibble requires 0 parameter(s) but found 1.\n" +
			"line 5:0 Constructor MyClass() does not initialise field 'ref'.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithUnknownFullyQualifiedPrimaryType()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"java.lang.Wibble<String> ref;\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}\n";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Unrecognised type: java.lang.Wibble is not a type.\n" +
				"line 3:0 Type java.lang.Wibble requires 0 parameter(s) but found 1.\n" +
				"line 5:0 Constructor MyClass() does not initialise field 'ref'.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithUnknownTypeParameter()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String<Wibble> ref;\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
			"}\n";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Type java.lang.String requires 0 parameter(s) but found 1.\n" +
			"line 3:7 Unrecognised type: Wibble is not a type.\n" +
			"line 5:0 Constructor MyClass() does not initialise field 'ref'.\n",
			result.getErrors());
	}

	@Test
	public void testFieldInElseBlock()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"if (true)\n" +
					"{\n" +
					"}\n" +
					"else\n" +
					"{\n" +
						"this.out.println();\n" +
					"}\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals("line 10:4 Unknown field 'out'.\n",
			result.getErrors());
	}

	@Test
	public void testReadFieldBeforeDeclaration()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass() {}\n" +
				"public Void method()\n" +
				"{\n" +
					"String thingy = this.thingy;\n" +
				"}\n" +
				"String thingy = \"thingy\";\n" +
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
	public void testRestUnconstructedField()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String thingy = this.sumpt;\n" +
				"String sumpt = this.thingy;\n" +
				"public MyClass() {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:20 Cannot read unconstructed field 'sumpt'.\n" +
			"line 4:19 Cannot read unconstructed field 'thingy'.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithMisplacedTypeParameters()
	{
		String source =
			"import java.util.Map;\n" +
			"import java.util.HashMap;\n" +
			"class MyClass\n" +
			"{\n" +
				"Map map<Num, Num> = new HashMap<Num, Num>();" +
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

		assertFalse(result.isSuccessful());
	}
}
