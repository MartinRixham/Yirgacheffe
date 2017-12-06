package yirgacheffe.compiler;

import org.junit.Test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class YirgacheffeTest
{
	@Test
	public void testMainMethodOnSuccess() throws Exception
	{
		PrintStream originalOut = System.out;
		PrintStream originalError = System.err;

		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setOut(out);
		System.setErr(error);

		Yirgacheffe.main(new String[] {"class MyClass {}"});

		assertTrue(spyOut.toString().length() > 0);
		assertTrue(spyError.toString().length() == 0);

		System.setOut(originalOut);
		System.setErr(originalError);
	}

	@Test
	public void testMainMethodOnError() throws Exception
	{
		PrintStream originalOut = System.out;
		PrintStream originalError = System.err;

		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setOut(out);
		System.setErr(error);

		Yirgacheffe.main(new String[] {"thingy MyClass {}"});

		assertTrue(spyOut.toString().length() == 0);
		assertEquals(
			"line 1:0 Expected declaration of class or interface.\n",
			spyError.toString());

		System.setOut(originalOut);
		System.setErr(originalError);
	}

	@Test
	public void testNamedEmptyInterface() throws Exception
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("interface MyInterface {}");

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;

		assertTrue(result.isSuccessful());
		assertEquals("MyInterface", classNode.name);
		assertEquals(access, classNode.access);
		assertEquals(0, classNode.fields.size());
	}

	@Test
	public void testFailToDeclareClassOrInterface() throws Exception
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("thingy MyInterface {}");

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Expected declaration of class or interface.\n",
			result.getErrors());
	}

	@Test
	public void testNamedEmptyClass() throws Exception
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("class MyClass {}");

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertTrue(result.isSuccessful());
		assertEquals("MyClass", classNode.name);
		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNode.access);
		assertEquals(0, classNode.fields.size());
	}

	@Test
	public void testClassWithIntegerField() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
					"int myField;\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;

		assertTrue(result.isSuccessful());
		assertEquals(1, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, firstField.access);
		assertEquals("I", firstField.desc);
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

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;

		assertTrue(result.isSuccessful());
		assertEquals(1, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals(Opcodes.ACC_PRIVATE, firstField.access);
		assertEquals("Ljava/lang/String;", firstField.desc);
		assertEquals("myStringField", firstField.name);
	}

	@Test
	public void testClassWithIntegerAndStringFields() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"int myIntegerField;\n" +
				"String myStringField;\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;

		assertTrue(result.isSuccessful());
		assertEquals(2, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals("I", firstField.desc);
		assertEquals("myIntegerField", firstField.name);

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
				"  int myField;\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

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

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:1 Field declaration should start with type.\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceWithMissingIdentifier() throws Exception
	{
		String source =
			"interface\n" +
				"{\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Interface identifier expected.\n",
			result.getErrors());
	}

	@Test
	public void testClassWithMissingIdentifier() throws Exception
	{
		String source =
			"class\n" +
				"{\n" +
				" int myField;\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 1:0 Class identifier expected.\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceWithMethod() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"int myMethod();\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		assertTrue(result.isSuccessful());
		assertEquals(1, methods.size());

		MethodNode firstMethod = methods.get(0);

		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, firstMethod.access);
		assertEquals("()I", firstMethod.desc);
		assertEquals("myMethod", firstMethod.name);
	}

	@Test
	public void testInterfaceMethodWithModifier() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"public int myInterfaceMethod();\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:0 Access modifier is not required for " +
				"interface method declaration.\n",
			result.getErrors());
	}

	@Test
	public void testInterfaceMethodWithParameters() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"int myParameterisedMethod(String param1, int param2);\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;
		MethodNode firstMethod = methods.get(0);

		assertTrue(result.isSuccessful());
		assertEquals("(Ljava/lang/String;I)I", firstMethod.desc);
		assertEquals("myParameterisedMethod", firstMethod.name);
	}

	@Test
	public void testParameterWithMissingType() throws Exception
	{
		String source =
			"interface MyInterface\n" +
				"{\n" +
				"int myMethod(param1, int param2);\n" +
				"}";

		Yirgacheffe yirgacheffe = new Yirgacheffe(source);

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 3:13 Expected type before argument identifier\n",
			result.getErrors());
	}
}
