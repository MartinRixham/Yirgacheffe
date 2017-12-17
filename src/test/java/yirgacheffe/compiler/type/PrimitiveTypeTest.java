package yirgacheffe.compiler.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrimitiveTypeTest
{
	@Test
	public void testVoidIsPrimitive()
	{
		Type type = PrimitiveType.VOID;

		assertEquals("void", type.toString());
		assertEquals("java.lang.Void", type.toFullyQualifiedType());
		assertEquals("V", type.toJVMType());
		assertEquals(0, type.width());
	}

	@Test
	public void testBoolIsPrimitive()
	{
		Type type = PrimitiveType.BOOL;

		assertEquals("bool", type.toString());
		assertEquals("java.lang.Boolean", type.toFullyQualifiedType());
		assertEquals("Z", type.toJVMType());
		assertEquals(1, type.width());
	}

	@Test
	public void testCharIsPrimitive()
	{
		Type type = PrimitiveType.CHAR;

		assertEquals("char", type.toString());
		assertEquals("java.lang.Character", type.toFullyQualifiedType());
		assertEquals("C", type.toJVMType());
		assertEquals(1, type.width());
	}

	@Test
	public void testNumIsPrimitive()
	{
		Type type = PrimitiveType.NUM;

		assertEquals("num", type.toString());
		assertEquals("java.lang.Double", type.toFullyQualifiedType());
		assertEquals("D", type.toJVMType());
		assertEquals(2, type.width());
	}
}
