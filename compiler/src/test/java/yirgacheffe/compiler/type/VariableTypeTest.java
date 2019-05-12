package yirgacheffe.compiler.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VariableTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier()
	{
		Class<?> loadedClass = Object.class;

		ReferenceType referenceType = new ReferenceType(loadedClass);

		Type type = new VariableType("T");

		assertEquals("T", type.toString());
		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("T", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals("TT;", type.getSignature());
		assertEquals(1, type.width());
		assertEquals(0, type.getReturnInstruction());
		assertEquals(0, type.getStoreInstruction());
		assertEquals(0, type.getLoadInstruction());
		assertEquals(0, type.getZero());
		assertFalse(type.isAssignableTo(new ReferenceType(Object.class)));
		assertTrue(type.hasParameter());
	}
}
