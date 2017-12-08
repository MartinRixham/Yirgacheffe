package yirgacheffe.compiler;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainTest
{
	@Test
	public void testMainMethodOnSuccess() throws Exception
	{
		new File("example/MyClass.class").delete();

		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setErr(error);

		Yirgacheffe.main(new String[] {"example/MyClass.yg"});

		assertTrue(spyError.toString().length() == 0);
		assertTrue(new FileInputStream("example/MyClass.class").read() != -1);

		System.setErr(originalError);
	}

	@Test
	public void testMainMethodOnError() throws Exception
	{
		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setErr(error);

		Yirgacheffe.main(new String[] {"example/Malformed.yg"});

		assertEquals(
			"line 1:17 Expected declaration of class or interface.\n",
			spyError.toString());

		System.setErr(originalError);
	}

	@Test
	public void testMainMethodOnParseError() throws Exception
	{
		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setErr(error);

		Yirgacheffe.main(new String[] {"example/Unparsable.yg"});

		assertEquals(1, spyError.toString().split("\n").length);
		assertEquals(
			"line 2:0 mismatched input",
			spyError.toString().substring(0, 25));

		System.setErr(originalError);
	}

	@Test
	public void testCompilingTwoClasses() throws Exception
	{
		new File("example/MyClass.class").delete();
		new File("example/MyInterface.class").delete();

		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setErr(error);

		Yirgacheffe.main(new String[] {"example/MyClass.yg", "example/MyInterface.yg"});

		assertTrue(spyError.toString().length() == 0);
		assertTrue(new FileInputStream("example/MyClass.class").read() != -1);

		System.setErr(originalError);
	}
}
