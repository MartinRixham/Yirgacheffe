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

public class TypeVarianceTest
{
	@Test
	public void testImplementingMethodWithTypeVariance() throws Exception
	{
		String interfaceSource =
			"interface Objectifier\n" +
			"{" +
				"Object objectify(String string);\n" +
			"}";

		String implementationSource =
			"class Stringifier implements Objectifier\n" +
			"{\n" +
				"public String objectify(Object obj) { return obj.toString(); }\n" +
				"public Stringifier() {}\n" +
			"}";

		String source =
			"class MyClass\n" +
			"{\n" +
				"main hello(Array<String> args)" +
				"{\n" +
					"Objectifier objectifier = new Stringifier();" +
					"new System().getOut().println(objectifier.objectify(args.get(0)));" +
				"}\n" +
			"}";

		Classes classes = new Classes();

		new Compiler("", interfaceSource).compileInterface(classes);
		new Compiler("", implementationSource).compileInterface(classes);

		classes.clearCache();

		Compiler interfaceCompiler = new Compiler("", interfaceSource);
		Compiler implementationCompiler = new Compiler("", implementationSource);
		Compiler compiler = new Compiler("", source);

		CompilationResult interfaceResult = interfaceCompiler.compile(classes);
		CompilationResult implementationResult = implementationCompiler.compile(classes);
		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("Objectifier", interfaceResult.getBytecode());
		classLoader.add("Stringifier", implementationResult.getBytecode());
		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method hello = myClass.getMethod("main", String[].class);
		String[] args = {"Eh up, planet."};

		hello.invoke(null, (Object) args);

		assertEquals("Eh up, planet.\n", spyOut.toString());

		System.setOut(originalOut);
	}

	@Test
	public void testImplementingMethodWithNumberReturnType() throws Exception
	{
		String implementationSource =
			"class Numberfier implements Comparable<String>\n" +
			"{\n" +
				"public Num compareTo(Object obj) { return obj.hashCode(); }\n" +
				"public Numberfier() {}\n" +
			"}";

		String source =
			"class MyClass\n" +
			"{\n" +
				"main hello(Array<String> args)" +
				"{\n" +
					"Comparable<String> comparable = new Numberfier();" +
					"new System().getOut().println(comparable.compareTo(args.get(0)));" +
				"}\n" +
			"}";

		Classes classes = new Classes();

		new Compiler("", implementationSource).compileInterface(classes);

		classes.clearCache();

		Compiler implementationCompiler = new Compiler("", implementationSource);
		Compiler compiler = new Compiler("", source);

		CompilationResult implementationResult = implementationCompiler.compile(classes);
		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
		assertTrue(implementationResult.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("Numberfier", implementationResult.getBytecode());
		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method hello = myClass.getMethod("main", String[].class);
		String[] args = {"Eh up, planet."};

		hello.invoke(null, (Object) args);

		assertEquals("5.598657E7\n", spyOut.toString());

		System.setOut(originalOut);
	}
}
