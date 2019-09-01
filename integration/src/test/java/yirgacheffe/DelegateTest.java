package yirgacheffe;

import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;

public class DelegateTest
{
	@Test
	public void testDelegation() throws Exception
	{
		String source =
			"class MyClass implements Comparable<String>\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
					"delegate(\"thingy\");\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		Class<?> myClass = classLoader.loadClass("MyClass");
		Object my = myClass.getConstructor().newInstance();
		Method compareTo = myClass.getMethod("compareTo", Object.class);

		int positiveNumber = (int) compareTo.invoke(my, "sumpt");
		int negativeNumber = (int) compareTo.invoke(my, "wibble");

		assertTrue(positiveNumber > 0);
		assertTrue(negativeNumber < 0);
	}
}
