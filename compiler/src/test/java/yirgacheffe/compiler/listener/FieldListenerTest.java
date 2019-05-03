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

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;

		assertEquals(2, methods.size());

		MethodNode initialiser = (MethodNode) methods.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, initialiser.access);
		assertEquals("0myStringField_init_field", initialiser.name);
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

		MethodNode constructor = (MethodNode) methods.get(1);

		assertEquals("<init>", constructor.name);

		instructions = constructor.instructions;

		VarInsnNode thirdInsn = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInsn.getOpcode());
		assertEquals(0, thirdInsn.var);

		MethodInsnNode fourthInsn = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInsn.getOpcode());
		assertEquals("0myStringField_init_field", fourthInsn.name);
		assertEquals("()V", fourthInsn.desc);
		assertEquals("MyClass", fourthInsn.owner);
		assertFalse(fourthInsn.itf);
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

		List methods = classNode.methods;
		MethodNode initialiser = (MethodNode) methods.get(0);
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

		List methods = classNode.methods;
		MethodNode initialiser = (MethodNode) methods.get(0);
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

		List methods = classNode.methods;
		MethodNode initialiser = (MethodNode) methods.get(0);
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

		List methods = classNode.methods;
		MethodNode initialiser = (MethodNode) methods.get(0);
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

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;

		assertEquals(2, methods.size());

		MethodNode initialiser = (MethodNode) methods.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, initialiser.access);
		assertEquals("0myObject_init_field", initialiser.name);
		assertEquals("()V", initialiser.desc);
		assertEquals(1, initialiser.maxLocals);
		assertEquals(3, initialiser.maxStack);

		InsnList instructions = initialiser.instructions;

		FieldInsnNode seventhInstruction = (FieldInsnNode) instructions.get(6);

		assertEquals(Opcodes.PUTFIELD, seventhInstruction.getOpcode());
		assertEquals("MyClass", seventhInstruction.owner);
		assertEquals("myObject", seventhInstruction.name);
		assertEquals("Ljava/lang/Object;", seventhInstruction.desc);

		MethodNode constructor = (MethodNode) methods.get(1);

		instructions = constructor.instructions;

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("0myObject_init_field", fourthInstruction.name);
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

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List methods = classNode.methods;

		assertEquals(3, methods.size());

		MethodNode first = (MethodNode) methods.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, first.access);
		assertEquals("0myObject_init_field", first.name);

		MethodNode second = (MethodNode) methods.get(1);

		assertEquals(Opcodes.ACC_PRIVATE, second.access);
		assertEquals("0myString_init_field", second.name);

		MethodNode constructor = (MethodNode) methods.get(2);

		InsnList instructions = constructor.instructions;

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		MethodInsnNode fourthInstruction = (MethodInsnNode) instructions.get(3);

		assertEquals(Opcodes.INVOKEVIRTUAL, fourthInstruction.getOpcode());
		assertEquals("0myObject_init_field", fourthInstruction.name);
		assertEquals("MyClass", fourthInstruction.owner);
		assertEquals("()V", fourthInstruction.desc);

		VarInsnNode fifthInstruction = (VarInsnNode) instructions.get(4);

		assertEquals(Opcodes.ALOAD, fifthInstruction.getOpcode());
		assertEquals(0, fifthInstruction.var);

		MethodInsnNode sixthInstruction = (MethodInsnNode) instructions.get(5);

		assertEquals(Opcodes.INVOKEVIRTUAL, sixthInstruction.getOpcode());
		assertEquals("0myString_init_field", sixthInstruction.name);
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
				"String myStringField;\n" +
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

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List fields = classNode.fields;

		assertEquals(1, fields.size());

		List methods = classNode.methods;

		assertEquals(2, methods.size());

		MethodNode method = (MethodNode) methods.get(0);
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
			"line 5:19 Unknown field 'myStringField'.\n",
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
				"line 6:3 Method java.lang.Object.println(Num) not found.\n",
			result.getErrors());
	}
}
