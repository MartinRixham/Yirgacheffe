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

public class EnumerationTest
{
	@Before
	public void clearMethodCache()
	{
		Bootstrap.clearCache();
	}

	@Test
	public void testEnumeration() throws Exception
	{
		String enumerationSource =
			"enumeration MyNumeration of Bool" +
			"{" +
				"String message;\n" +
				"true:(\"The truth!\");\n" +
				"false:(\"Lies!\");\n" +
				"MyNumeration(String message)\n" +
				"{\n" +
					"this.message = message;\n" +
				"}\n" +
				"public String getMessage()\n" +
				"{\n" +
					"return this.message;\n" +
				"}\n" +
			"}";

		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"MyNumeration myEnum = MyNumeration:true;\n" +
					"new System().getOut().println(myEnum.getMessage());\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();

		Compiler enumerationCompiler = new Compiler("", enumerationSource);
		Compiler compiler = new Compiler("", source);

		enumerationCompiler.compileClassDeclaration(classes);
		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		enumerationCompiler.compileInterface(classes);
		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult enumerationResult = enumerationCompiler.compile(classes);
		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyNumeration", enumerationResult.getBytecode());
		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method method = myClass.getMethod("main", String[].class);
		String[] args = {};

		method.invoke(null, (Object) args);

		assertEquals("The truth!\n", spyOut.toString());

		System.setOut(originalOut);
	}
}
