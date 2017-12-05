package yirgacheffe;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import org.junit.Test;

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
	public void testNamedEmptyClass()
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("class MyClass {}");

		byte[] bytecode = yirgacheffe.compile();

		ClassReader reader = new ClassReader(bytecode);
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		assertEquals("MyClass", classNode.name);
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
	}
}
