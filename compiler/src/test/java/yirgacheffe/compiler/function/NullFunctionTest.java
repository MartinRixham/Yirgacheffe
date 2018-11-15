package yirgacheffe.compiler.function;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NullFunctionTest
{
	@Test
	public void testGettingStringPrintlnMethod()
	{
		Callable function = new NullFunction("MyClass.method");

		assertEquals("MyClass.method", function.getName());
		assertEquals("()V", function.getDescriptor());
		assertEquals(0, function.getParameterTypes().length());
		assertEquals(0, function.checkTypeParameters(null).length());
		assertTrue(function.isPublic());
	}
}
