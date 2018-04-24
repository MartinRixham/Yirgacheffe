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

public class MainTest
{
	@Test
	public void testMainMethod() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main hello(Array<String> args)" +
				"{\n" +
					"new System().getOut().println(args.get(0));" +
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
		Method hello = myClass.getMethod("main", String[].class);
		String[] args = {"Eh up, planet."};

		hello.invoke(null, (Object) args);

		assertEquals("Eh up, planet.\n", sypOut.toString());

		java.lang.System.setOut(originalOut);
	}
}
