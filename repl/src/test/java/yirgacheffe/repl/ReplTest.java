package yirgacheffe.repl;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		PrintStream originalError = System.err;
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(err);
		System.setErr(error);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);
		InputStream in = new ByteArrayInputStream("\n".getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals("yirgacheffe> yirgacheffe> ", out.toString());
		assertEquals("", err.toString());

		System.setErr(originalError);
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
	public void testTwoStatements()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);

		InputStream in =
			new ByteArrayInputStream(
				("String thingy = \"thingy\";String sumpt = \"sumpt\";\n" +
				"sumpt").getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertEquals("yirgacheffe> yirgacheffe> sumpt\nyirgacheffe> ", out.toString());
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
			"yirgacheffe> Missing ';'.\nyirgacheffe> ",
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
			"yirgacheffe> Missing ';'.\nyirgacheffe> yirgacheffe> ",
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
	public void testVariableDeclarationAndAssignment()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);

		InputStream in =
			new ByteArrayInputStream(
				("String thingy;\nthingy = \"thingy\";\nthingy").getBytes());

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
				"Unrecognised type: java.lang.ArrayList is not a type.\n" +
				"yirgacheffe> ",
			out.toString());
	}

	@Test
	public void testNonsenseInput()
	{
		PrintStream originalError = System.err;
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(err);
		System.setErr(error);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);

		InputStream in = new ByteArrayInputStream("What's going on?".getBytes());

		Repl repl = new Repl(stream);

		repl.read(in);

		assertTrue(out.toString().length() > 0);
		assertEquals("", err.toString());

		System.setErr(originalError);
	}
}
