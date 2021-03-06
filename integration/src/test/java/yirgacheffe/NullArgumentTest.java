package yirgacheffe;

import org.junit.Before;
import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Bootstrap;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NullArgumentTest
{
	@Before
	public void clearMethodCache()
	{
		Bootstrap.clearCache();
	}

	@Test
	public void testNullArgument() throws Exception
	{
		String source =
			"import java.io.PrintStream;\n" +
			"class MyClass\n" +
			"{\n" +
				"PrintStream out = new IO().getOut();\n" +
				"public MyClass() {}\n" +
				"public Void hello()\n" +
				"{\n" +
					"String string = new MutableReference<String>().get();\n" +
					"string = this.process(string);\n" +
					"if (!string)\n" +
					"{\n" +
						"this.out.println(\"Got nothin.\");\n" +
					"}\n" +
				"}\n" +
				"public String process(String string)" +
				"{\n" +
					"return \"Processed...\";\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Object my = myClass.getConstructor().newInstance();
		Method hello = myClass.getMethod("hello");

		hello.invoke(my);

		assertEquals("Got nothin.\n", spyOut.toString());

		System.setOut(originalOut);
	}

	@Test
	public void testCallMethodOnNull() throws Exception
	{
		String source =
			"import java.io.PrintStream;\n" +
			"class MyClass\n" +
			"{\n" +
				"PrintStream out = new IO().getOut();\n" +
				"public MyClass() {}\n" +
				"public Void hello()\n" +
				"{\n" +
					"String string = new MutableReference<String>().get();\n" +
					"string = string.concat(\"\");\n" +
					"if (!string)\n" +
					"{\n" +
						"this.out.println(\"Got nothin.\");\n" +
					"}\n" +
				"}\n" +
				"public String process(String string)" +
				"{\n" +
					"return \"Processed...\";\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Object my = myClass.getConstructor().newInstance();
		Method hello = myClass.getMethod("hello");

		hello.invoke(my);

		assertEquals("Got nothin.\n", spyOut.toString());

		System.setOut(originalOut);
	}
}
