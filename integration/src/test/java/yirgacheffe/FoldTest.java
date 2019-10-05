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

public class FoldTest
{
	@Before
	public void clearMethodCache()
	{
		Bootstrap.clearCache();
	}

	@Test
	public void testFold() throws Exception
	{
		String foldSource =
			"class Fold<R implements Combinable<S>, S>\n" +
			"{\n" +
				"Array<R> array;\n" +
				"public Fold(Array<R> array)\n" +
				"{\n" +
					"this.array = array;\n" +
				"}\n" +
				"public S with(S item)\n" +
				"{\n" +
					"for (Num i = 0; i < this.array.length(); i++)\n" +
					"{\n" +
						"item = this.array.get(i).combineWith(item);\n" +
					"}\n" +
					"return item;\n" +
				"}\n" +
			"}";

		String implementationSource =
			"class Werd implements Combinable<String>\n" +
			"{\n" +
				"String werd;\n" +
				"public Werd(String werd) { this.werd = werd; }\n" +
				"public String combineWith(String other)\n" +
				"{\n" +
					"return other + \" \" + this.werd;\n" +
				"}\n" +
			"}";

		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)\n" +
				"{\n" +
					"Array<Werd> sentence =" +
						"new Array<Werd>(new Werd(\"it\"), new Werd(\"worked\"));\n" +
					"String werds = new Fold<Werd, String>(sentence).with(\"yes\");\n" +
					"new System().getOut().println(werds);\n" +
				"}\n" +
			"}";

		Classes classes = new Classes();

		Compiler foldCompiler = new Compiler("", foldSource);
		Compiler implementationCompiler = new Compiler("", implementationSource);
		Compiler compiler = new Compiler("", source);

		foldCompiler.compileClassDeclaration(classes);
		implementationCompiler.compileClassDeclaration(classes);
		compiler.compileClassDeclaration(classes);

		classes.clearCache();

		foldCompiler.compileInterface(classes);
		implementationCompiler.compileInterface(classes);
		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult interfaceResult = foldCompiler.compile(classes);
		CompilationResult implementationResult = implementationCompiler.compile(classes);
		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("Fold", interfaceResult.getBytecode());
		classLoader.add("Werd", implementationResult.getBytecode());
		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Method method = myClass.getMethod("main", String[].class);
		String[] args = {};

		method.invoke(null, (Object) args);

		assertEquals("yes it worked\n", spyOut.toString());

		System.setOut(originalOut);
	}
}
