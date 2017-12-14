package yirgacheffe.compiler.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NullTypeTest
{
	@Test
	public void testNullType()
	{
		Type type = new NullType();

		assertEquals("V", type.toJVMType());
		assertEquals("void", type.toFullyQualifiedType());
		assertEquals(1, type.width());
	}
}
