package yirgacheffe.compiler;

import org.junit.Test;
import yirgacheffe.compiler.main.Yirgacheffe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
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

		assertTrue(spyError.toString().length() > 0);

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
			"line 1:40 extraneous input",
			spyError.toString().substring(0, 26));

		System.setErr(originalError);
	}

	@Test
	public void testCompilingTwoInterfaces() throws Exception
	{
		new File("example/MyInterface.class").delete();
		new File("example/AnotherClass.class").delete();
		new File("example/more/MoreInterface.class").delete();

		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);
		String[] arguments =
			new String[]
				{
					"example/MyInterface.yg",
					"example/AnotherClass.yg",
					"example/more/MoreInterface.yg"
				};

		System.setErr(error);

		Yirgacheffe.main(arguments);

		InputStream firstFile =
			new FileInputStream("example/MyInterface.class");
		InputStream secondFile =
			new FileInputStream("example/AnotherClass.class");
		InputStream thirdFile =
			new FileInputStream("example/more/MoreInterface.class");

		assertTrue(spyError.toString().length() == 0);
		assertTrue(firstFile.read() != -1);
		assertTrue(secondFile.read() != -1);
		assertTrue(thirdFile.read() != -1);

		System.setErr(originalError);
	}
}
