package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ReferenceTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier()
	{
		Class<?> loadedClass = java.lang.String.class;

		Type type = new ReferenceType(loadedClass);

		assertEquals("java.lang.String", type.toString());
		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("java.lang.String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/String;", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
	}

	@Test
	public void testStringIsAssignableToObject()
	{
		Class<?> stringClass = java.lang.String.class;
		Class<?> objectClass = java.lang.Object.class;

		Type string = new ReferenceType(stringClass);
		Type object = new ReferenceType(objectClass);

		assertTrue(string.isAssignableTo(object));
	}

	@Test
	public void testStringIsNotAssignableToSystem()
	{
		Class<?> stringClass = java.lang.String.class;
		Class<?> systemClass = java.lang.System.class;

		Type string = new ReferenceType(stringClass);
		Type system = new ReferenceType(systemClass);

		assertFalse(string.isAssignableTo(system));
	}
}
