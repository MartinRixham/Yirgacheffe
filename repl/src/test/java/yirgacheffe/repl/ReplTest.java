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

		assertEquals("yirgacheffe> thingy\nyirgacheffe> ", out.toString());
	}

	@Test
	public void testPrintNumber()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);
		InputStream in = new ByteArrayInputStream("12.34".getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals("yirgacheffe> 12.34\nyirgacheffe> ", out.toString());
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
			"yirgacheffe> thingy\nyirgacheffe> sumpt\nyirgacheffe> ",
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

	@Test
	public void testAssigningAndPrintingVariable()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);

		InputStream in =
			new ByteArrayInputStream("String thingy = \"thingy\";\nthingy".getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals("yirgacheffe> yirgacheffe> thingy\nyirgacheffe> ", out.toString());
	}

	@Test
	public void testInvalidStatement()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);

		InputStream in =
			new ByteArrayInputStream("String thingy = \"thingy\"".getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals(
			"yirgacheffe> line 3:50 Missing ';'.\nyirgacheffe> ",
			out.toString());
	}

	@Test
	public void testCorrectedStatement()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);

		InputStream in =
			new ByteArrayInputStream(
				"String thingy = \"thingy\"\nString thingy = \"thingy\";".getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals(
			"yirgacheffe> line 3:50 Missing ';'.\nyirgacheffe> yirgacheffe> ",
			out.toString());
	}

	@Test
	public void testMultipleAssignments()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);

		InputStream in =
			new ByteArrayInputStream(
				("String thingy = \"thingy\";\n" +
					"String sumpt = \"sumpt\";\n" +
					"thingy").getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals(
			"yirgacheffe> yirgacheffe> yirgacheffe> thingy\nyirgacheffe> ",
			out.toString());
	}

	@Test
	public void testImportArray()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);

		InputStream in =
			new ByteArrayInputStream(
				("import java.util.ArrayList;\n" +
					"new ArrayList<String>()").getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals(
			"yirgacheffe> yirgacheffe> []\nyirgacheffe> ",
			out.toString());
	}

	@Test
	public void testUnknownImport()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);

		InputStream in =
			new ByteArrayInputStream(
				("import java.lang.ArrayList;").getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals(
			"yirgacheffe> " +
				"line 1:7 Unrecognised type: java.lang.ArrayList is not a type.\n" +
				"yirgacheffe> ",
			out.toString());
	}
}
