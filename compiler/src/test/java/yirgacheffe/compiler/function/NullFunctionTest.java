package yirgacheffe.compiler.function;

import org.junit.Test;
import yirgacheffe.compiler.type.NullType;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NullFunctionTest
{
	@Test
	public void testNullFunction()
	{
		Function function = new NullFunction();

		assertEquals("", function.getName());
		assertEquals("()V", function.getDescriptor());
		assertTrue(function.getOwner() instanceof NullType);
		assertTrue(function.getReturnType() instanceof NullType);
		assertTrue(function.getSignature() instanceof NullSignature);
		assertEquals(new Array<>(), function.getGenericParameterTypes());
		assertEquals(new Array<>(), function.getParameterTypes());
		assertFalse(function.hasVariableArguments());
		assertTrue(function.isNamed(""));
		assertFalse(function.isStatic());
	}
}
