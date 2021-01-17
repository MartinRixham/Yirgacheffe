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

public class BooleanOperationTest
{
	@Before
	public void clearMethodCache()
	{
		Bootstrap.clearCache();
	}

	@Test
	public void testBooleanOperation() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main hello(Array<String> args)" +
				"{\n" +
					"if (false || 0 || 0.0 || \"\" || try(0) || true)" +
					"{\n" +
						"if (true && 1 && 1.1 && \"A\" && try(5) && true)" +
						"\n{" +
							"if (try(this.getString()) || try(this.getString()))\n" +
							"{\n" +
								"this.printer(args);\n" +
							"}\n" +
						"\n}" +
					"}\n" +
				"}\n" +
				"public String getString()\n" +
				"{\n" +
					"return new Exception(\"Catch me.\");\n" +
				"}\n" +
				"public Void printer(Array<Object> objs)\n" +
				"{\n" +
					"new System().getOut().println(objs.get(0));\n" +
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

		Exception exception = null;

		try
		{
			hello.invoke(null, (Object) args);
		}
		catch (Exception e)
		{
			exception = e;
		}

		assertEquals("Catch me.", exception.getCause().getMessage());
		assertEquals("", spyOut.toString());

		System.setOut(originalOut);
	}
}
