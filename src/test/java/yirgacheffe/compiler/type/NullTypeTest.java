package yirgacheffe.compiler.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NullTypeTest
{
	@Test
	public void testNullType()
	{
		Type type = new NullType();

		assertEquals("I", type.toJVMType());
		assertEquals("java.lang.Integer", type.toFullyQualifiedType());
		assertEquals(1, type.width());
	}
}
