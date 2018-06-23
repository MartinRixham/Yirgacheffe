package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenericTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier()
	{
		Class<?> loadedClass = String.class;

		Type type = new GenericType(new ReferenceType(loadedClass));

		assertEquals("java.lang.String", type.toString());
		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("java.lang.String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertTrue(type.isAssignableTo(new ReferenceType(java.lang.String.class)));
	}
}
