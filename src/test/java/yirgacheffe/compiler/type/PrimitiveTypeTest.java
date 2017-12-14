package yirgacheffe.compiler.type;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrimitiveTypeTest
{
	@Test
	public void testVoidIsPrimitive()
	{
		assertTrue(PrimitiveType.isPrimitive("void"));

		Type type = new PrimitiveType("void");

		assertEquals("void", type.toFullyQualifiedType());
		assertEquals("V", type.toJVMType());
		assertEquals(1, type.width());
	}

	@Test
	public void testBoolIsPrimitive()
	{
		assertTrue(PrimitiveType.isPrimitive("bool"));

		Type type = new PrimitiveType("bool");

		assertEquals("bool", type.toFullyQualifiedType());
		assertEquals("B", type.toJVMType());
		assertEquals(1, type.width());
	}

	@Test
	public void testCharIsPrimitive()
	{
		assertTrue(PrimitiveType.isPrimitive("char"));

		Type type = new PrimitiveType("char");

		assertEquals("char", type.toFullyQualifiedType());
		assertEquals("C", type.toJVMType());
		assertEquals(1, type.width());
	}

	@Test
	public void testNumIsPrimitive()
	{
		assertTrue(PrimitiveType.isPrimitive("num"));

		Type type = new PrimitiveType("num");

		assertEquals("num", type.toFullyQualifiedType());
		assertEquals("D", type.toJVMType());
		assertEquals(2, type.width());
	}

	@Test
	public void testIntIsNotPrimitive()
	{
		assertFalse(PrimitiveType.isPrimitive("int"));

		Type type = new PrimitiveType("char");

		assertEquals("char", type.toFullyQualifiedType());
		assertEquals("C", type.toJVMType());
	}

	@Test
	public void testTypesWithSameNameAreEqual()
	{
		assertTrue(new PrimitiveType("bool").equals(new PrimitiveType("bool")));
	}

	@Test
	public void testTypesWithDifferentNamesAreUnqual()
	{
		assertFalse(new PrimitiveType("bool").equals(new PrimitiveType("char")));
	}

	@Test
	public void testGetHashCode()
	{
		assertTrue(new PrimitiveType("bool").hashCode() == "bool".hashCode());
	}
}
