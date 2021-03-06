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

public class InterfaceConstructorTest
{
	@Before
	public void clearMethodCache()
	{
		Bootstrap.clearCache();
	}

	@Test
	public void testInterfaceConstructor() throws Exception
	{
		String interfaceSource =
			"interface Amenable\n" +
			"{\n" +
				"Amenable()\n" +
				"{\n" +
					"return this(new Amen());\n" +
				"}\n" +
				"Amenable(Amenable amenable)\n" +
				"{\n" +
					"return amenable;\n" +
				"}\n" +
			"}";

		String implementationSource =
			"class Amen implements Amenable\n" +
			"{\n" +
				"public Amen()\n" +
				"{\n" +
				"}\n" +
				"public String toString()\n" +
				"{\n" +
					"return \"Are you amenable?\";\n" +
				"}\n" +
			"}";

		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"Amenable amenable = new Amenable();\n" +
					"new IO().getOut().println(amenable);\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();

		Compiler interfaceCompiler = new Compiler("", interfaceSource);
		Compiler implementationCompiler = new Compiler("", implementationSource);
		Compiler compiler = new Compiler("", source);

		interfaceCompiler.compileClassDeclaration(classes);
		implementationCompiler.compileClassDeclaration(classes);
		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		interfaceCompiler.compileInterface(classes);
		implementationCompiler.compileInterface(classes);
		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult interfaceResult = interfaceCompiler.compile(classes);
		CompilationResult implementationResult = implementationCompiler.compile(classes);
		CompilationResult result = compiler.compile(classes);

		assertTrue(interfaceResult.isSuccessful());
		assertTrue(implementationResult.isSuccessful());
		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("Amenable", interfaceResult.getBytecode());
		classLoader.add("Amen", implementationResult.getBytecode());
		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method method = myClass.getMethod("main", String[].class);
		String[] args = {};

		method.invoke(null, (Object) args);

		assertEquals("Are you amenable?\n", spyOut.toString());

		System.setOut(originalOut);
	}
}
