package yirgacheffe.compiler.function;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NullFunctionTest
{
	@Test
	public void testGettingStringPrintlnMethod()
	{
		Callable function = new NullFunction();

		assertEquals("", function.getName());
		assertEquals("()V", function.getDescriptor());
		assertEquals(0, function.getParameterTypes().size());
		assertEquals(0, function.checkTypeParameters(null).size());
	}
}
