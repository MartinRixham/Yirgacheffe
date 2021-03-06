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

public class BranchTest
{
	@Before
	public void clearMethodCache()
	{
		Bootstrap.clearCache();
	}

	@Test
	public void testBranching() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
				"public Void method()" +
				"{\n" +
					"this.branch(true);" +
					"this.branch(false);" +
				"}\n" +
				"public Void branch(Bool bool)" +
				"{\n" +
					"if (bool)" +
					"{" +
						"new IO().getOut().println(\"yes\");" +
					"}" +
					"else" +
					"{" +
						"new IO().getOut().println(\"no\");" +
					"}" +
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
		Object my = myClass.getConstructor().newInstance();
		Method hello = myClass.getMethod("method");

		hello.invoke(my);

		assertEquals("yes\nno\n", spyOut.toString());

		System.setOut(originalOut);
	}
}
