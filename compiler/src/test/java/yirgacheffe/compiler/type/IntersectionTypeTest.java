package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class IntersectionTypeTest
{
	@Test
	public void testIntersectionOfStringAndDouble()
	{
		Type firstType = new ReferenceType(String.class);
		Type secondType = PrimitiveType.DOUBLE;

		Type type = new IntersectionType(firstType, secondType);

		assertEquals("java.lang.String", type.toString());
		assertEquals(String.class, type.reflectionClass());
		assertEquals("java/lang/String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/String;", type.toJVMType());
		assertEquals("Ljava/lang/String;", type.getSignature());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertFalse(type.hasParameter());
		assertFalse(type.isPrimitive());
		assertNotEquals(type, new ReferenceType(String.class));
		assertEquals(type.hashCode(), secondType.hashCode());
	}

	@Test
	public void testIntersectionOfDoubleAndString()
	{
		Type firstType = PrimitiveType.DOUBLE;
		Type secondType = new ReferenceType(String.class);

		Type type = new IntersectionType(firstType, secondType);

		assertEquals("Num", type.toString());
		assertFalse(type.hasParameter());
		assertEquals(1, type.width());
		assertFalse(type.isPrimitive());
		assertEquals(type, new ReferenceType(String.class));
		assertEquals(type.hashCode(), secondType.hashCode());
	}

	@Test
	public void testIntersectionOfDoubleAndBoolean()
	{
		Type firstType = PrimitiveType.DOUBLE;
		Type secondType = PrimitiveType.BOOLEAN;

		Type type = new IntersectionType(firstType, secondType);

		assertEquals("Num", type.toString());
		assertFalse(type.hasParameter());
		assertEquals(1, type.width());
		assertFalse(type.isPrimitive());
		assertEquals(type.hashCode(), secondType.hashCode());
	}

	@Test
	public void testIntersectionOfDoubleAndInteger()
	{
		Type firstType = PrimitiveType.DOUBLE;
		Type secondType = PrimitiveType.INT;

		Type type = new IntersectionType(firstType, secondType);

		assertEquals("Num", type.toString());
		assertFalse(type.hasParameter());
		assertEquals(2, type.width());
		assertTrue(type.isPrimitive());
		assertEquals(type, PrimitiveType.INT);
		assertEquals(type.hashCode(), secondType.hashCode());
	}

	@Test
	public void testIntersectionIsAssignableToCommonSupertype()
	{
		Type arrayList = new ReferenceType(java.util.ArrayList.class);
		Type linkedList = new ReferenceType(java.util.LinkedList.class);
		Type list = new ReferenceType(java.util.List.class);

		Type type = new IntersectionType(arrayList, linkedList);

		assertFalse(type.isAssignableTo(arrayList));
		assertFalse(type.isAssignableTo(linkedList));
		assertTrue(type.isAssignableTo(list));
		assertNotEquals(type, new ReferenceType(Object.class));
	}

}
