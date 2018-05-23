package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ReferenceTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier() throws Exception
	{
		Class<?> loadedClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		Type type = new ReferenceType(loadedClass);

		assertEquals("java.lang.String", type.toString());
		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("java.lang.String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/String;", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertTrue(type.hasTypeParameter(String.class));
	}

	@Test
	public void testStringIsAssignableToObject() throws Exception
	{
		Class<?> stringClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		Class<?> objectClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.Object");

		Type string = new ReferenceType(stringClass);
		Type object = new ReferenceType(objectClass);

		assertTrue(string.isAssignableTo(object));
	}

	@Test
	public void testStringIsNotAssignableToSystem() throws Exception
	{
		Class<?> stringClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		Class<?> systemClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("yirgacheffe.lang.System");

		Type string = new ReferenceType(stringClass);
		Type system = new ReferenceType(systemClass);

		assertFalse(string.isAssignableTo(system));
	}
}
