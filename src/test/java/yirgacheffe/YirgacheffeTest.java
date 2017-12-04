package yirgacheffe;

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

		Yirgacheffe.main(new String[] {""});

		assertTrue(spyOut.toString().length() > 0);

		System.setOut(originalOut);
	}

	@Test
	public void testNamedEmptyClass()
	{
		Yirgacheffe yirgacheffe = new Yirgacheffe("class MyClass {}");

		byte[] bytecode = yirgacheffe.compile();

		ClassPrinter classPrinter = new ClassPrinter(bytecode);
		String printedClass = classPrinter.print();

		assertEquals("MyClass extends java/lang/Object {\n}\n", printedClass);
	}
}
