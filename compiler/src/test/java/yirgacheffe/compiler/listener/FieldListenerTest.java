package yirgacheffe.compiler.listener;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
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
	public void testClassWithNumberField() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"num myField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, firstField.access);
		assertEquals("D", firstField.desc);
		assertEquals("myField", firstField.name);
	}

	@Test
	public void testClassWithStringField() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String myStringField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, firstField.access);
		assertEquals("Ljava/lang/String;", firstField.desc);
		assertEquals("myStringField", firstField.name);
	}

	@Test
	public void testClassWithNumberAndStringFields() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"num myNumberField;\n" +
				"String myStringField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;

		assertEquals(2, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals("D", firstField.desc);
		assertEquals("myNumberField", firstField.name);

		FieldNode secondField = fields.get(1);

		assertEquals("Ljava/lang/String;", secondField.desc);
		assertEquals("myStringField", secondField.name);
	}

	@Test
	public void testInterfaceWithField() throws Exception
	{
		String source =
			"interface MyClass\n" +
			"{\n" +
				"  num myField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:2 Interface cannot contain field.\n",
			result.getErrors());
	}

	@Test
	public void testClassFieldWithMissingType() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				" myField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:1 Field declaration should start with type.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithUnknownType() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"Thingy myStringField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Unrecognised type: Thingy is not a type.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithFullyQualifiedType() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"java.util.List myListField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;
		String descriptor = fields.get(0).desc;

		assertEquals("Ljava/util/List;", descriptor);
	}

	@Test
	public void testFieldWithUnknownFullyQualifiedType() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"java.thingy.List myListField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Unrecognised type: java.thingy.List is not a type.\n",
			result.getErrors());
	}

	@Test
	public void testFieldWithImportedType() throws Exception
	{
		String source =
			"import java.util.List;\n" +
			"class MyClass\n" +
			"{\n" +
				"List myListField;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;
		String descriptor = fields.get(0).desc;

		assertEquals("Ljava/util/List;", descriptor);
	}

	@Test
	public void testStringFieldWithInitialiser() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String myStringField = \"thingy\";\n" +
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

		List<MethodNode> methods = classNode.methods;
		MethodNode initialiser = methods.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, initialiser.access);
		assertEquals("0_init_field", initialiser.name);
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

		FieldInsnNode thirInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.PUTFIELD, thirInstruction.getOpcode());
		assertEquals("MyClass", thirInstruction.owner);
		assertEquals("myStringField", thirInstruction.name);
		assertEquals("Ljava/lang/String;", thirInstruction.desc);

		assertEquals(Opcodes.RETURN, instructions.get(3).getOpcode());
	}

	@Test
	public void testNumberFieldWithIntegerInitialiser() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"num myNumberField = 5;\n" +
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

		List<MethodNode> methods = classNode.methods;
		MethodNode initialiser = methods.get(0);
		InsnList instructions = initialiser.instructions;

		assertEquals(4, instructions.size());

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(5.0, secondInstruction.cst);

		FieldInsnNode thirdInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.PUTFIELD, thirdInstruction.getOpcode());
		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("myNumberField", thirdInstruction.name);
		assertEquals("D", thirdInstruction.desc);
	}

	@Test
	public void testNumberFieldWithDecimalInitialiser() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"num myNumberField = 1.2;\n" +
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

		List<MethodNode> methods = classNode.methods;
		MethodNode initialiser = methods.get(0);
		InsnList instructions = initialiser.instructions;

		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(1.2, secondInstruction.cst);
	}

	@Test
	public void testCharacterFieldWithInitialiser() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"char myCharacterField = 'a';\n" +
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

		List<MethodNode> methods = classNode.methods;
		MethodNode initialiser = methods.get(0);
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
	public void testBooleanFieldWithInitialiser() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"bool myBooleanField = true;\n" +
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

		List<MethodNode> methods = classNode.methods;
		MethodNode initialiser = methods.get(0);
		InsnList instructions = initialiser.instructions;
		LdcInsnNode secondInstruction = (LdcInsnNode) instructions.get(1);

		assertEquals(1, secondInstruction.cst);

		FieldInsnNode thirdInstruction = (FieldInsnNode) instructions.get(2);

		assertEquals(Opcodes.PUTFIELD, thirdInstruction.getOpcode());
		assertEquals("MyClass", thirdInstruction.owner);
		assertEquals("myBooleanField", thirdInstruction.name);
		assertEquals("Z", thirdInstruction.desc);
	}

	@Test
	public void testFieldInitialiserWithMismatchedTypes() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"num myBooleanField = true;\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Cannot assign bool to field of type num.\n",
			result.getErrors());
	}

	@Test
	public void testReadFromStringField() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"String myStringField;\n" +
				"public void read()\n" +
				"{\n" +
					"String read = this.myStringField;\n" +
				"}" +
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

		List<FieldNode> fields = classNode.fields;

		assertEquals(1, fields.size());

		List<MethodNode> methods = classNode.methods;

		assertEquals(2, methods.size());

		MethodNode method = methods.get(0);
		InsnList instructions = method.instructions;

		assertEquals(4, instructions.size());

		VarInsnNode firstInstruction = (VarInsnNode) instructions.get(0);

		assertEquals(Opcodes.ALOAD, firstInstruction.getOpcode());
		assertEquals(0, firstInstruction.var);

		FieldInsnNode secondInstruction = (FieldInsnNode) instructions.get(1);

		assertEquals(Opcodes.GETFIELD, secondInstruction.getOpcode());
		assertEquals("MyClass", secondInstruction.owner);
		assertEquals("myStringField", secondInstruction.name);
		assertEquals("Ljava/lang/String;", secondInstruction.desc);

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ASTORE, thirdInstruction.getOpcode());
		assertEquals(1, thirdInstruction.var);
	}
}
