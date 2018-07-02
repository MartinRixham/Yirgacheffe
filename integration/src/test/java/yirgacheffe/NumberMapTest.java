package yirgacheffe;

import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NumberMapTest
{
	@Test
	public void testNumberMap() throws Exception
	{
		String source =
			"import java.util.HashMap;\n" +
			"import java.util.Map;\n" +
			"class MyClass\n" +
			"{\n" +
				"main map(Array<String> args)" +
				"{\n" +
					"Map<Num,Num> map = new HashMap<Num,Num>();\n" +
					"map.put(1, 2);\n" +
					"Num two = map.get(1);\n" +
					"new System().getOut().println(two);\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("", source);

		compiler.compileClassDeclaration(classes);

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
		Method map = myClass.getMethod("map", Array.class);

		map.invoke(my, new Array<String>());

		assertEquals("2.0\n", spyOut.toString());

		System.setOut(originalOut);
	}
}
