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
import static org.junit.Assert.assertTrue;

public class YirgacheffeTest
{
	@Test
	public void testMainMethod() throws Exception
	{
		PrintStream originalOut = System.out;

		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(spyOut);

		System.setOut(printStream);

		Yirgacheffe.main(new String[] {"class MyClass {}"});

		assertTrue(spyOut.toString().length() > 0);

		System.setOut(originalOut);
	}

	@Test
	public void testNamedEmptyInterface()
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("interface MyInterface {}");

		byte[] bytecode = yirgacheffe.compile();

		ClassReader reader = new ClassReader(bytecode);
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;

		assertEquals("MyInterface", classNode.name);
		assertEquals(access, classNode.access);
		assertEquals(0, classNode.fields.size());
	}

	@Test
	public void testNamedEmptyClass()
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("class MyClass {}");

		byte[] bytecode = yirgacheffe.compile();

		ClassReader reader = new ClassReader(bytecode);
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

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

		byte[] bytecode = yirgacheffe.compile();

		ClassReader reader = new ClassReader(bytecode);
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals("MyClass", classNode.name);
		assertEquals(Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNode.access);

		List<FieldNode> fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals("myField", firstField.name);
		assertEquals(Opcodes.ACC_PRIVATE, firstField.access);
	}
}
