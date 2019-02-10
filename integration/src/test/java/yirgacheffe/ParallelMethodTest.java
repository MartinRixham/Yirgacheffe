package yirgacheffe;

import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.parallel.GeneratedClass;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParallelMethodTest
{
	@Test
	public void testParallelMethod() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"Comparable<String> string = this.getString();\n" +
					"Num comparison = string.compareTo(\"sumpt\");\n" +
					"new System().getOut().println(comparison);\n" +
				"}\n" +
				"parallel public Comparable<String> getString()\n" +
				"{\n" +
					"return \"thingy\";\n" +
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

		GeneratedClass generatedClass = result.getGeneratedClasses().get(0);

		classLoader.add(
			generatedClass.getClassName(), generatedClass.getBytecode());

		PrintStream originalOut = java.lang.System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		java.lang.System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method hello = myClass.getMethod("main", String[].class);
		String[] args = new String[0];

		hello.invoke(null, (Object) args);

		assertEquals("1.0\n", spyOut.toString());

		java.lang.System.setOut(originalOut);
	}

}
