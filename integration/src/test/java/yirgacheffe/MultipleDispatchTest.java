package yirgacheffe;

import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultipleDispatchTest
{
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

				"public Object getString()" +
				"{" +
					"return \"thingy\";" +
				"}" +

				"public Bool equals(String other)" +
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
}
