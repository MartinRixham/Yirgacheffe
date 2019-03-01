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

public class LoopTest
{
	@Test
	public void testForLoop() throws Exception
	{
		String source =
			"import java.io.PrintStream;\n" +
			"class MyClass\n" +
			"{\n" +
				"PrintStream out = new System().getOut();\n" +
				"public Void hello()\n" +
				"{\n" +
					"for (Num i = 0; i < 4; i = i + 1)\n" +
					"{\n" +
						"this.out.println(i);" +
					"}\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

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

		assertEquals("0.0\n1.0\n2.0\n3.0\n", spyOut.toString());

		System.setOut(originalOut);
	}
}
