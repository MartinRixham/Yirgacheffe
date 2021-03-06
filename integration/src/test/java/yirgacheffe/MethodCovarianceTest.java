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

public class MethodCovarianceTest
{
	@Before
	public void clearMethodCache()
	{
		Bootstrap.clearCache();
	}

	@Test
	public void testMethodCovariance() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main hello(Array<String> args)" +
				"{\n" +
					"this.printer(args);" +
				"}\n" +
				"public Void printer(Array<Object> objs)\n" +
				"{\n" +
					"new IO().getOut().println(objs.get(0));\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileClassDeclaration(classes);

		classes.clearCache();

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
		Method hello = myClass.getMethod("main", String[].class);
		String[] args = {"Eh up, planet."};

		hello.invoke(null, (Object) args);

		assertEquals("Eh up, planet.\n", spyOut.toString());

		System.setOut(originalOut);
	}
}
