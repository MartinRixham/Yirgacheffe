package yirgacheffe;

import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TailCallOptimisationTest
{
	@Test
	public void testTailCallOptimisation() throws Exception
	{
		String source =
			"import java.io.PrintStream;\n" +
			"class MyClass\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
				"public Num fib(Num n, Num a, Num b)" +
				"{\n" +
					"if (n == 0.0)\n" +
					"{" +
						"return b;" +
					"}\n" +
					"return this.fib(n - 1.0, b, a + b);" +
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

		Class<?> myClass = classLoader.loadClass("MyClass");
		Object my = myClass.getConstructor().newInstance();

		Method hello =
			myClass.getMethod("fib", double.class, double.class, double.class);

		Object output = hello.invoke(my, 4, 1, 1);

		assertEquals(8.0, output);
	}
}
