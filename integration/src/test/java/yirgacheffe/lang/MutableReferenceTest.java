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

public class MutableReferenceTest
{
	@Test
	public void testMutableReference() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"MutableReference<String> myString =" +
					"new MutableReference<String>(\"Hello world.\");\n" +
				"public void hello()" +
				"{\n" +
					"new System().getOut().println(this.myString.get());\n" +
					"this.myString.set(\"Eh up, planet.\");\n" +
					"new System().getOut().println(this.myString.get());\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

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
		Object my = myClass.getConstructor().newInstance();
		Method hello = myClass.getMethod("hello");

		hello.invoke(my);

		assertEquals("Hello world.\nEh up, planet.\n", spyOut.toString());

		java.lang.System.setOut(originalOut);
	}
}
