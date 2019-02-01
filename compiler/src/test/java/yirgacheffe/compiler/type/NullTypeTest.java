package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NullTypeTest
{
	@Test
	public void testNullType()
	{
		Type type = new NullType();

		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals(null, type.getSignature());
		assertEquals("java.lang.Object", type.toFullyQualifiedType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertEquals("java.lang.Object", type.toString());
		assertFalse(type.hasParameter());
	}

	@Test
	public void testNullIsAssignableToString() throws Exception
	{
		Class<?> stringClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		Type string = new ReferenceType(stringClass);

		assertTrue(new NullType().isAssignableTo(string));
	}

	@Test
	public void testNullIsAssignableToBoolean()
	{
		assertTrue(new NullType().isAssignableTo(PrimitiveType.BOOLEAN));
	}
}
