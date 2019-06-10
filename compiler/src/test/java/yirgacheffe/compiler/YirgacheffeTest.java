package yirgacheffe.compiler;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class YirgacheffeTest
{
	@Test
	public void testMainMethodOnSuccess() throws Exception
	{
		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setErr(error);

		Yirgacheffe.main(new String[] {"example/MyClass.yg"});

		assertEquals("", spyError.toString());
		assertTrue(new FileInputStream("example/MyClass.class").read() != -1);

		System.setErr(originalError);

		new File("example/MyClass.class").delete();
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

		assertEquals(
			"Errors in file example/Unparsable.yg:\n" +
			"line 1:40 mismatched input '{'",
			spyError.toString().substring(0, 68));

		System.setErr(originalError);
	}

	@Test
	public void testCompilingMultipleInterfaces() throws Exception
	{
		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);
		String[] arguments =
			new String[]
				{
					"example/AnotherClass.yg",
					"example/more/MoreInterface.yg",
					"example/MyInterface.yg"
				};

		System.setErr(error);

		Yirgacheffe.main(arguments);

		InputStream firstFile =
			new FileInputStream("example/MyInterface.class");
		InputStream secondFile =
			new FileInputStream("example/AnotherClass.class");
		InputStream thirdFile =
			new FileInputStream("example/more/MoreInterface.class");

		assertEquals("", spyError.toString());
		assertTrue(firstFile.read() != -1);
		assertTrue(secondFile.read() != -1);
		assertTrue(thirdFile.read() != -1);

		System.setErr(originalError);

		new File("example/MyInterface.class").delete();
		new File("example/AnotherClass.class").delete();
		new File("example/more/MoreInterface.class").delete();
	}

	@Test
	public void testMismatchedPackages() throws Exception
	{
		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);
		String[] arguments =
			new String[]
				{
					"example/MyClass.yg",
					"example/more/MoreClass.yg"
				};

		System.setErr(error);

		Yirgacheffe.main(arguments);

		assertEquals(
			"Errors in file example/more/MoreClass.yg:\n" +
			"line 9:6 Unrecognised type: MyClass is not a type.\n" +
			"line 12:1 Method requires method body.\n",
			spyError.toString());

		System.setErr(originalError);

		new File("example/MyClass.class").delete();
		new File("example/more/MoreClass.class").delete();
	}

	@Test
	public void testThreadClassGeneratedForParallelMethod() throws Exception
	{
		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setErr(error);

		Yirgacheffe.main(new String[] {"example/Parallel.yg"});

		assertEquals("", spyError.toString());
		assertTrue(new FileInputStream("example/Parallel.class").read() != -1);
		assertTrue(new FileInputStream("example/Parallel$getString.class").read() != -1);

		System.setErr(originalError);

		new File("example/Parallel.class").delete();
		new File("example/Parallel$getString.class").delete();
	}

	@Test
	public void testImplementingInterface() throws Exception
	{
		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);

		System.setErr(error);

		Yirgacheffe.main(new String[] {"example/myinterface/MyInterface.yg"});

		assertEquals("", spyError.toString());
		assertNotEquals(
			-1,
			new FileInputStream("example/myinterface/MyInterface.class").read());

		Yirgacheffe.main(new String[] {"example/myinterface/MyImplementation.yg"});

		assertEquals("", spyError.toString());
		assertNotEquals(
			-1,
			new FileInputStream("example/myinterface/MyImplementation.class").read());

		System.setErr(originalError);

		new File("example/myinterface/MyInterface.class").delete();
		new File("example/myinterface/MyImplementation.class").delete();
	}

	@Test
	public void testCircularDependency() throws Exception
	{
		PrintStream originalError = System.err;
		ByteArrayOutputStream spyError = new ByteArrayOutputStream();
		PrintStream error = new PrintStream(spyError);
		String[] arguments =
			new String[]
				{
					"example/codependency/A.yg",
					"example/codependency/B.yg",
				};

		System.setErr(error);

		Yirgacheffe.main(arguments);

		assertEquals("", spyError.toString());

		InputStream firstFile =
			new FileInputStream("example/codependency/A.class");
		InputStream secondFile =
			new FileInputStream("example/codependency/B.class");

		assertTrue(firstFile.read() != -1);
		assertTrue(secondFile.read() != -1);

		System.setErr(originalError);

		new File("example/codependency/A.class").delete();
		new File("example/codependency/B.class").delete();
	}
}
