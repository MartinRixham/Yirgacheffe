package yirgacheffe.compiler;

import org.junit.Test;
import yirgacheffe.compiler.Type.BytecodeClassLoader;
import yirgacheffe.compiler.main.CompilationResult;
import yirgacheffe.compiler.main.Compiler;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class StatementTest
{
	@Test
	public void testLocalVariableDeclaration() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
					"public MyClass()" +
					"{\n" +
						"num myVariable;\n" +
					"}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertTrue(result.isSuccessful());
	}

	@Test
	public void testLocalVariableInitialisation() throws Exception
	{
		String source =
			"class MyClass\n" +
				"{\n" +
				"public MyClass()" +
				"{\n" +
				"num myVariable = 1;\n" +
				"}\n" +
				"}";

		Compiler compiler = new Compiler("", source);
		CompilationResult result =
			compiler.compile(new HashMap<>(), new BytecodeClassLoader());

		assertTrue(result.isSuccessful());
	}
}
