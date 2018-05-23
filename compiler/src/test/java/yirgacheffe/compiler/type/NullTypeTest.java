package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NullTypeTest
{
	@Test
	public void testNullType()
	{
		Type type = new NullType();

		assertEquals("I", type.toJVMType());
		assertEquals("java.lang.Integer", type.toFullyQualifiedType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.IRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ISTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ILOAD, type.getLoadInstruction());
		assertTrue(type.hasTypeParameter(String.class));
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
	public void testNullIsAssignableToBoolean() throws Exception
	{
		assertTrue(new NullType().isAssignableTo(PrimitiveType.BOOLEAN));
	}
}
