package yirgacheffe;

import org.junit.Before;
import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Bootstrap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExceptionTest
{
	@Before
	public void clearMethodCache()
	{
		Bootstrap.clearCache();
	}

	@Test
	public void uncaughtException() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"main method(Array<String> args)" +
				"{" +
					"Num number = try this.getNumber();\n" +
					"this.handle(number);" +
				"}\n" +
				"public Num getNumber()" +
				"{\n" +
					"return new Exception();\n" +
				"}\n" +
				"public Void handle(Num number) {}\n" +
				"public Void handle(RuntimeException e) {}\n" +
			"}";

		Classes classes = new Classes();
		Compiler compiler = new Compiler("MyClass.yg", source);

		compiler.compileInterface(classes);

		classes.clearCache();

		CompilationResult result = compiler.compile(classes);

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		Class<?> myClass = classLoader.loadClass("MyClass");
		Object my = myClass.getConstructor().newInstance();
		Method map = myClass.getMethod("method", Array.class);

		StackTraceElement[] stackTrace = new StackTraceElement[0];

		try
		{
			map.invoke(my, new Array<String>());
		}
		catch (InvocationTargetException e)
		{
			stackTrace = e.getCause().getStackTrace();
		}

		assertEquals("MyClass.getNumber(MyClass.yg:6)", stackTrace[0].toString());
	}
}
