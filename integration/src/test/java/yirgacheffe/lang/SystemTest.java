package yirgacheffe.lang;

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

public class SystemTest
{
	@Test
	public void testOut() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public void hello()" +
				"{\n" +
					"new System().getOut().println(\"Eh up, planet.\");" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = java.lang.System.out;
		ByteArrayOutputStream sypOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(sypOut);

		java.lang.System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Object my = myClass.getConstructor().newInstance();
		Method hello = myClass.getMethod("hello");

		hello.invoke(my);

		assertEquals("Eh up, planet.\n", sypOut.toString());

		java.lang.System.setOut(originalOut);
	}
}
