package yirgacheffe.repl;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ReplTest
{
	@Test
	public void testPrompt()
	{
		PrintStream originalOut = System.out;
		InputStream originalIn = System.in;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		InputStream in = new ByteArrayInputStream("\n".getBytes());
		System.setIn(in);

		Repl.main(null);

		assertEquals("yirgacheffe> \n", spyOut.toString());

		System.setOut(originalOut);
		System.setIn(originalIn);
	}

	@Test
	public void testPrintString()
	{
		PrintStream originalOut = System.out;
		InputStream originalIn = System.in;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		InputStream in = new ByteArrayInputStream("\"thingy\"".getBytes());
		System.setIn(in);

		Repl.main(null);

		assertEquals("yirgacheffe> \"thingy\"\n", spyOut.toString());

		System.setOut(originalOut);
		System.setIn(originalIn);
	}

	@Test
	public void testPrintingTwoStrings()
	{
		PrintStream originalOut = System.out;
		InputStream originalIn = System.in;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		InputStream in = new ByteArrayInputStream("\"thingy\"\n\"sumpt\"".getBytes());
		System.setIn(in);

		Repl.main(null);

		assertEquals(
			"yirgacheffe> \"thingy\"\nyirgacheffe> \"sumpt\"\n",
			spyOut.toString());

		System.setOut(originalOut);
		System.setIn(originalIn);
	}
}
