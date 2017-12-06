package yirgacheffe.compiler;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainTest
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
	public void testMainMethodOnParseError() throws Exception
	{
		PrintStream originalOut = System.out;
		PrintStream originalError = System.err;

		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setOut(out);
		System.setErr(error);

		Yirgacheffe.main(new String[] {"interface MyInterface {"});

		assertTrue(spyOut.toString().length() == 0);
		assertEquals(
			"line 1:23 mismatched input '<EOF>' expecting {'}', Type, Modifier}\n",
			spyError.toString());

		System.setOut(originalOut);
		System.setErr(originalError);
	}
}
