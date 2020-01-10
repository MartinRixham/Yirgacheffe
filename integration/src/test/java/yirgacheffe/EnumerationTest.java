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
			"class MyNumeration enumerates Bool" +
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

	@Test
	public void testEnumerationWithDefault() throws Exception
	{
		String enumerationSource =
			"class MyNumeration enumerates Bool" +
			"{" +
				"String message;\n" +
				"true:(\"The truth!\");\n" +
				"false:(\"Lies!\");\n" +
				"MyNumeration()\n" +
				"{\n" +
					"this.message = \"\";\n" +
				"}\n" +
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
					"MyNumeration myEnum = MyNumeration:(0 == 0);\n" +
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

	@Test
	public void testInterfaceEnumeration() throws Exception
	{
		String implementationSource =
			"class MyImplementation implements MyInterface" +
			"{" +
				"String message;\n" +
				"public MyImplementation(String message)\n" +
				"{\n" +
					"this.message = message;\n" +
				"}\n" +
				"public String getMessage()\n" +
				"{\n" +
					"return this.message;\n" +
				"}\n" +
			"}";

		String enumerationSource =
			"interface MyInterface enumerates Bool" +
			"{" +
				"true:(\"The truth!\");\n" +
				"false:(\"Lies!\");\n" +
				"MyInterface(String message)\n" +
				"{\n" +
					"return new MyImplementation(message);\n" +
				"}\n" +
				"String getMessage();\n" +
			"}";

		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"MyInterface myEnum = MyInterface:true;\n" +
					"new System().getOut().println(myEnum.getMessage());\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();

		Compiler implementationCompiler = new Compiler("", implementationSource);
		Compiler enumerationCompiler = new Compiler("", enumerationSource);
		Compiler compiler = new Compiler("", source);

		implementationCompiler.compileClassDeclaration(classes);
		enumerationCompiler.compileClassDeclaration(classes);
		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		implementationCompiler.compileInterface(classes);
		enumerationCompiler.compileInterface(classes);
		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult implementationResult = implementationCompiler.compile(classes);
		CompilationResult enumerationResult = enumerationCompiler.compile(classes);
		CompilationResult result = compiler.compile(classes);

		assertTrue(implementationResult.isSuccessful());
		assertTrue(enumerationResult.isSuccessful());
		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyImplementation", implementationResult.getBytecode());
		classLoader.add("MyInterface", enumerationResult.getBytecode());
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

	@Test
	public void testInterfaceEnumerationWithDefault() throws Exception
	{
		String implementationSource =
			"class MyImplementation implements MyInterface" +
			"{" +
				"String message;\n" +
				"public MyImplementation(String message)\n" +
				"{\n" +
					"this.message = message;\n" +
				"}\n" +
				"public String getMessage()\n" +
				"{\n" +
					"return this.message;\n" +
				"}\n" +
			"}";

		String enumerationSource =
			"interface MyInterface enumerates Bool" +
			"{" +
				"true:(\"The truth!\");\n" +
				"false:(\"Lies!\");\n" +
				"MyInterface()\n" +
				"{\n" +
					"return new MyImplementation(\"\");\n" +
				"}\n" +
				"MyInterface(String message)\n" +
				"{\n" +
					"return new MyImplementation(message);\n" +
				"}\n" +
				"String getMessage();\n" +
			"}";

		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"MyInterface myEnum = MyInterface:(0 == 0);\n" +
					"new System().getOut().println(myEnum.getMessage());\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();

		Compiler implementationCompiler = new Compiler("", implementationSource);
		Compiler enumerationCompiler = new Compiler("", enumerationSource);
		Compiler compiler = new Compiler("", source);

		implementationCompiler.compileClassDeclaration(classes);
		enumerationCompiler.compileClassDeclaration(classes);
		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		implementationCompiler.compileInterface(classes);
		enumerationCompiler.compileInterface(classes);
		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult implementationResult = implementationCompiler.compile(classes);
		CompilationResult enumerationResult = enumerationCompiler.compile(classes);
		CompilationResult result = compiler.compile(classes);

		assertTrue(implementationResult.isSuccessful());
		assertTrue(enumerationResult.isSuccessful());
		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyImplementation", implementationResult.getBytecode());
		classLoader.add("MyInterface", enumerationResult.getBytecode());
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
