package yirgacheffe;

import org.junit.Test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

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
		assertTrue(spyError.toString().length() > 0);

		System.setOut(originalOut);
		System.setErr(originalError);
	}

	@Test
	public void testNamedEmptyInterface()
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("interface MyInterface {}");

		CompilationResult result = yirgacheffe.compile();

		ClassReader reader = new ClassReader(result.getBytecode());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;

		assertEquals("MyInterface", classNode.name);
		assertEquals(access, classNode.access);
		assertEquals(0, classNode.fields.size());
	}

	@Test
	public void testFailToDeclareClassOrInterface()
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("thingy MyInterface {}");

		CompilationResult result = yirgacheffe.compile();

		assertFalse(result.isSuccessful());
		assertEquals("Declaration should be of class or interface.", result.getErrors());
	}

	@Test
	public void testNamedEmptyClass()
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
	public void testClassWithIntegerField()
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

		assertTrue(result.isSuccessful());
		assertEquals("MyClass", classNode.name);
		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNode.access);

		List<FieldNode> fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals("myField", firstField.name);
		assertEquals(Opcodes.ACC_PRIVATE, firstField.access);
	}

	@Test
	public void testClassWithStringField()
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

		assertTrue(result.isSuccessful());
		assertEquals("MyClass", classNode.name);
		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNode.access);

		List<FieldNode> fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals("myStringField", firstField.name);
		assertEquals(Opcodes.ACC_PRIVATE, firstField.access);
	}
}
