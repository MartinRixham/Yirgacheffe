package yirgacheffe.compiler.listener;

import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.Classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeListenerTest
{
	@Test
	public void testMissingConstructorTypeParameter()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"MutableReference ref;\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:0 Missing type parameters for type" +
				" yirgacheffe.lang.MutableReference.\n",
			result.getErrors());
	}

	@Test
	public void testNotEnoughTypeParameters()
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()" +
				"{\n" +
					"new MutableReference<>(\"thingy\");\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result = compiler.compile(new Classes());

		assertFalse(result.isSuccessful());
		assertEquals(
			"line 4:4 Type yirgacheffe.lang.MutableReference" +
				" requires 1 parameter(s) but found 0.",
			result.getErrors().split("\n")[0]);
	}

	@Test
	public void testParameterisedReturnType()
	{
		String source =
			"import java.util.Map;\n" +
			"import java.util.HashMap;\n" +
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"this.getMap().get(\"hello\");\n" +
				"}\n" +
				"public Map<String, String> getMap()\n" +
				"{\n" +
					"return new HashMap<String, String>();\n" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("", source);
		Classes classes = new Classes();

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());
	}
}
