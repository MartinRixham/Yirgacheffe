package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenericTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier()
	{
		Class<?> loadedClass = String.class;

		ReferenceType referenceType = new ReferenceType(loadedClass);

		Type type = new GenericType(referenceType);

		assertEquals("java.lang.String", type.toString());
		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("java/lang/String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals("Ljava/lang/String;", type.getSignature());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertTrue(type.isAssignableTo(new ReferenceType(java.lang.String.class)));
		assertFalse(type.hasParameter());
		assertFalse(type.isPrimitive());
		assertEquals(type, referenceType);
		assertEquals(type.hashCode(), referenceType.hashCode());
	}
}
