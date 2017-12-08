package yirgacheffe.compiler;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler("", inputStream);
		CompilationResult result = compiler.compile();

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

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler("", inputStream);
		CompilationResult result = compiler.compile();

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

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler("", inputStream);
		CompilationResult result = compiler.compile();

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

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler("", inputStream);
		CompilationResult result = compiler.compile();

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

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler("", inputStream);
		CompilationResult result = compiler.compile();

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

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler("", inputStream);
		CompilationResult result = compiler.compile();

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

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler("", inputStream);
		CompilationResult result = compiler.compile();

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testFieldWithUnknownFullyQualifiedType() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"java.thingy.List myListField;\n" +
				"}";

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler("", inputStream);
		CompilationResult result = compiler.compile();

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

		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		Compiler compiler = new Compiler("", inputStream);
		CompilationResult result = compiler.compile();

		assertTrue(result.isSuccessful());
	}
}
