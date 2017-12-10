package yirgacheffe.compiler;

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
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.main.CompilationResult;
import yirgacheffe.compiler.main.Compiler;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class FieldTest
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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode constructor = methods.get(0);
		InsnList instructions = constructor.instructions;

		assertEquals(6, instructions.size());

		assertEquals(Opcodes.ALOAD, instructions.get(0).getOpcode());
		assertEquals(Opcodes.INVOKESPECIAL, instructions.get(1).getOpcode());

		VarInsnNode thirdInstruction = (VarInsnNode) instructions.get(2);

		assertEquals(Opcodes.ALOAD, thirdInstruction.getOpcode());
		assertEquals(0, thirdInstruction.var);

		LdcInsnNode fourthInstruction = (LdcInsnNode) instructions.get(3);

		assertEquals("thingy", fourthInstruction.cst);

		FieldInsnNode fifthInstruction = (FieldInsnNode) instructions.get(4);

		assertEquals(Opcodes.PUTFIELD, fifthInstruction.getOpcode());
		assertEquals("MyClass", fifthInstruction.owner);
		assertEquals("myStringField", fifthInstruction.name);
		assertEquals("Ljava/lang/String;", fifthInstruction.desc);

		assertEquals(Opcodes.RETURN, instructions.get(5).getOpcode());
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
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertTrue(result.isSuccessful());

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode constructor = methods.get(0);
		InsnList instructions = constructor.instructions;

		assertEquals(6, instructions.size());

		LdcInsnNode fourthInstruction = (LdcInsnNode) instructions.get(3);

		assertEquals(5.0, fourthInstruction.cst);

		FieldInsnNode fifthInstruction = (FieldInsnNode) instructions.get(4);

		assertEquals(Opcodes.PUTFIELD, fifthInstruction.getOpcode());
		assertEquals("MyClass", fifthInstruction.owner);
		assertEquals("myNumberField", fifthInstruction.name);
		assertEquals("D", fifthInstruction.desc);
	}
}
