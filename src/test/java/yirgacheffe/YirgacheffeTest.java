package yirgacheffe;

import org.junit.Test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

		assertEquals("MyInterface", classNode.name);

		int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;

		assertEquals(access, classNode.access);
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
	}
}
