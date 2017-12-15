package yirgacheffe.compiler.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrimitiveTypeTest
{
	@Test
	public void testVoidIsPrimitive()
	{
		Type type = PrimitiveType.VOID;

		assertEquals("java.lang.Void", type.toFullyQualifiedType());
		assertEquals("V", type.toJVMType());
		assertEquals(0, type.width());
	}

	@Test
	public void testBoolIsPrimitive()
	{
		Type type = PrimitiveType.BOOL;

		assertEquals("java.lang.Boolean", type.toFullyQualifiedType());
		assertEquals("B", type.toJVMType());
		assertEquals(1, type.width());
	}

	@Test
	public void testCharIsPrimitive()
	{
		Type type = PrimitiveType.CHAR;

		assertEquals("java.lang.Character", type.toFullyQualifiedType());
		assertEquals("C", type.toJVMType());
		assertEquals(1, type.width());
	}

	@Test
	public void testNumIsPrimitive()
	{
		Type type = PrimitiveType.NUM;

		assertEquals("java.lang.Double", type.toFullyQualifiedType());
		assertEquals("D", type.toJVMType());
		assertEquals(2, type.width());
	}
}
