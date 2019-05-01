package yirgacheffe;

import org.junit.Test;
import yirgacheffe.compiler.CompilationResult;
import yirgacheffe.compiler.Compiler;
import yirgacheffe.compiler.type.BytecodeClassLoader;
import yirgacheffe.compiler.type.Classes;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NullReferenceTest
{
	@Test
	public void testNullReference() throws Exception
	{
		String source =
			"class MyClass\n" +
			"{\n" +
				"public MyClass()\n" +
				"{\n" +
				"}\n" +
				"public Void hello()" +
				"{\n" +
					"new MutableReference<String>().get().toString();" +
				"}\n" +
			"}";

		Compiler compiler = new Compiler("MyClass.yg", source);
		CompilationResult result = compiler.compile(new Classes());

		assertTrue(result.isSuccessful());

		BytecodeClassLoader classLoader = new BytecodeClassLoader();

		classLoader.add("MyClass", result.getBytecode());

		PrintStream originalOut = System.out;
		ByteArrayOutputStream spyOut = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(spyOut);

		System.setOut(out);

		Class<?> myClass = classLoader.loadClass("MyClass");
		Object my = myClass.getConstructor().newInstance();
		Method hello = myClass.getMethod("hello");

		Throwable exception = null;

		try
		{
			hello.invoke(my);
		}
		catch (InvocationTargetException e)
		{
			exception = e.getCause();
		}

		assertEquals(
			"MyClass.hello(MyClass.yg:7)",
			exception.getStackTrace()[0].toString());

		System.setOut(originalOut);
	}
}
