package yirgacheffe.lang;

import org.junit.Ignore;
import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MutableReferenceTest
{
	@Test
	@Ignore
	public void testMutableReference() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"MutableReference myString =" +
					"new MutableReference(\"Hello world.\");\n" +
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

		try (OutputStream outputStream = new FileOutputStream("MyClass.class"))
		{
			outputStream.write(result.getBytecode());
		}

		PrintStream originalOut = java.lang.System.out;
		ByteArrayOutputStream sypOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(sypOut);

		java.lang.System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Object my = myClass.getConstructor().newInstance();
		Method hello = myClass.getMethod("hello");

		hello.invoke(my);

		assertEquals("Hello world.\nEh up, planet.\n", sypOut.toString());

		java.lang.System.setOut(originalOut);
	}
}
