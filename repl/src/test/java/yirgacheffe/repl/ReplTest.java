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

		InputStream in = new ByteArrayInputStream("".getBytes());
		System.setIn(in);

		Repl.main(null);

		assertEquals("yirgacheffe> ", spyOut.toString());

		System.setOut(originalOut);
		System.setIn(originalIn);
	}

	@Test
	public void testPrintString()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);
		InputStream in = new ByteArrayInputStream("\"thingy\"".getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals("yirgacheffe> \"thingy\"\nyirgacheffe> ", out.toString());
	}

	@Test
	public void testPrintingTwoStrings()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);
		InputStream in = new ByteArrayInputStream("\"thingy\"\n\"sumpt\"".getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals(
			"yirgacheffe> \"thingy\"\nyirgacheffe> \"sumpt\"\nyirgacheffe> ",
			out.toString());
	}

	@Test
	public void testPrintingEmptyLine()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);
		InputStream in = new ByteArrayInputStream("\n".getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals("yirgacheffe> yirgacheffe> ", out.toString());
	}
}
