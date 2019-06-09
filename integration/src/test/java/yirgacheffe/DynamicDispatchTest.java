package yirgacheffe;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Bootstrap;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DynamicDispatchTest
{
	@Before
	public void clearMethodCache()
	{
		Bootstrap.clearCache();
	}

	@Test
	public void testMultipleDispatch() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main hello(Array<String> args)" +
				"{\n" +
					"MyClass my = this;" +
					"Object string = this.getString();" +
					"Bool equal = my.equals(string);" +
					"new System().getOut().println(equal);" +
				"}\n" +

				"private Object getString()" +
				"{" +
					"return \"thingy\";" +
				"}" +

				"private Bool equals(String other)" +
				"{" +
					"return true;" +
				"}" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = java.lang.System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		java.lang.System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method hello = myClass.getMethod("main", String[].class);
		String[] args = {};

		hello.invoke(null, (Object) args);

		assertEquals("true\n", spyOut.toString());

		java.lang.System.setOut(originalOut);
	}

	@Test
	public void testMatchingMethodWithTwoParameters() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main hello(Array<String> args)" +
				"{\n" +
					"MyClass my = this;" +
					"Object string = this.getString();" +
					"Bool equal = my.getTruth(string, string);" +
					"new System().getOut().println(equal);" +
				"}\n" +

				"public Object getString()" +
				"{" +
					"return \"thingy\";" +
				"}" +

				"public Bool getTruth(Object first, String second)" +
				"{" +
					"return true;" +
				"}" +

				"public Bool getTruth(Object first, Object second)" +
				"{" +
					"return false;" +
				"}" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = java.lang.System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		java.lang.System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method hello = myClass.getMethod("main", String[].class);
		String[] args = {};

		hello.invoke(null, (Object) args);

		assertEquals("true\n", spyOut.toString());

		java.lang.System.setOut(originalOut);
	}

	@Test
	public void testOr() throws Exception
	{
		String source =
			"import java.io.PrintStream;\n" +
			"class MyClass\n" +
			"{\n" +
				"PrintStream out = new System().getOut();\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"Object obj = 1 || true;\n" +
					"this.print(obj);\n" +
				"}\n" +
				"private Void print(Object value)\n" +
				"{\n" +
					"this.out.println(\"--------- object ----------\");\n" +
				"}\n" +
				"private Void print(Num value)\n" +
				"{\n" +
					"this.out.println(\"--------- number ----------\");\n" +
				"}\n" +
				"private Void print(Bool value)\n" +
				"{\n" +
					"this.out.println(\"--------- boolean ----------\");\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = java.lang.System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		java.lang.System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method hello = myClass.getMethod("main", String[].class);
		String[] args = {};

		hello.invoke(null, (Object) args);

		assertEquals("--------- number ----------\n", spyOut.toString());

		java.lang.System.setOut(originalOut);
	}

	@Ignore
	@Test
	public void testDispatchPerformance() throws Exception
	{
		String source =
			"import java.io.PrintStream;\n" +
			"class MyClass\n" +
			"{\n" +
				"PrintStream out = new System().getOut();\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"Object obj = 1;\n" +
					"for (Num i = 0; i < 10000000; i++)\n" +
					"{\n" +
						"this.out.print(obj);\n" +
					"}\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method hello = myClass.getMethod("main", String[].class);
		String[] args = {};

		long startTime = getCPUTime();
		hello.invoke(null, (Object) args);
		long endTime = getCPUTime();

		System.out.println(
			"---------- execution took " +
				Duration.ofNanos(endTime - startTime).getSeconds() +
				" seconds ----------");
	}

	private static long getCPUTime()
	{
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

		return threadMXBean.getCurrentThreadCpuTime();
	}
}

// previous runs
// ---------- execution took 50 seconds ----------
// ---------- execution took 40 seconds ----------
// ---------- execution took 38 seconds ----------
