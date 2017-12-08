package yirgacheffe.compiler;

import org.junit.Test;
import yirgacheffe.main.Yirgacheffe;

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

		Yirgacheffe.main(new String[] {"example/MyClass.yg"});

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

		Yirgacheffe.main(new String[] {"example/Malformed.yg"});

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

		Yirgacheffe.main(new String[] {"example/Unparsable.yg"});

		assertTrue(spyOut.toString().length() == 0);
		assertEquals(1, spyError.toString().split("\n").length);
		assertEquals(
			"line 2:0 mismatched input",
			spyError.toString().substring(0, 25));

		System.setOut(originalOut);
		System.setErr(originalError);
	}
}
