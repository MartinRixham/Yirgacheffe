package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ArrayTypeTest
{
	@Test
	public void testArray()
	{
		Type string = new ArrayType("[Ljava.lang.String;");

		assertEquals("java.lang.String[]", string.toString());
		assertEquals("[Ljava.lang.String;", string.reflectionClass().getName());
		assertEquals("[Ljava/lang/String;", string.toJVMType());
		assertEquals("java.lang.String[]", string.toFullyQualifiedType());
		assertEquals(1, string.width());
		assertEquals(Opcodes.ARETURN, string.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, string.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, string.getLoadInstruction());
		assertTrue(string.hasTypeParameter(String.class));
	}

	@Test
	public void testArrayIsAssignableToArray()
	{
		Type first = new ArrayType("[Ljava.lang.Object;");
		Type second = new ArrayType("[Ljava.lang.String;");

		assertTrue(first.isAssignableTo(second));
	}

	@Test
	public void testArrayIsNotAssignableToAnythingElse()
	{
		Type first = new ArrayType("[Ljava.lang.Object;");
		Type second = PrimitiveType.BOOLEAN;

		assertFalse(first.isAssignableTo(second));
	}
}
