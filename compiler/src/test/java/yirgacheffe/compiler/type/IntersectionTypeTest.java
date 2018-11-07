package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class IntersectionTypeTest
{
	@Test
	public void testIntersectionOfTwoTypes()
	{
		Class<?> stringClass = String.class;

		Type firstType = new ReferenceType(String.class);
		Type secondType = PrimitiveType.DOUBLE;

		Type type = new IntersectionType(firstType, secondType);

		assertEquals("java.lang.String", type.toString());
		assertEquals(stringClass, type.reflectionClass());
		assertEquals("java.lang.String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/String;", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
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
	}

}
