package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PrimitiveTypeTest
{
	@Test
	public void testVoidIsPrimitive()
	{
		Type type = PrimitiveType.VOID;

		assertEquals("Void", type.toString());
		assertEquals("java.lang.Void", type.toFullyQualifiedType());
		assertEquals("V", type.toJVMType());
		assertEquals(0, type.width());
		assertEquals(Opcodes.RETURN, type.getReturnInstruction());
		assertEquals(Opcodes.NOP, type.getStoreInstruction());
		assertEquals(Opcodes.NOP, type.getLoadInstruction());
		assertEquals(Opcodes.NOP, type.getZero());
	}

	@Test
	public void testBoolIsPrimitive()
	{
		Type type = PrimitiveType.BOOLEAN;

		assertEquals("Bool", type.toString());
		assertEquals("java.lang.Boolean", type.toFullyQualifiedType());
		assertEquals("Z", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.IRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ISTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ILOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ICONST_0, type.getZero());
	}

	@Test
	public void testCharIsPrimitive()
	{
		Type type = PrimitiveType.CHAR;

		assertEquals("Char", type.toString());
		assertEquals("java.lang.Character", type.toFullyQualifiedType());
		assertEquals("C", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.IRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ISTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ILOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ICONST_0, type.getZero());
	}

	@Test
	public void testNumIsPrimitive()
	{
		Type type = PrimitiveType.DOUBLE;

		assertEquals("Num", type.toString());
		assertEquals("java.lang.Double", type.toFullyQualifiedType());
		assertEquals("D", type.toJVMType());
		assertEquals(2, type.width());
		assertEquals(Opcodes.DRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.DSTORE, type.getStoreInstruction());
		assertEquals(Opcodes.DLOAD, type.getLoadInstruction());
		assertEquals(Opcodes.DCONST_0, type.getZero());
	}

	@Test
	public void testPrimitiveIsAssignableToItself()
	{
		Type type = PrimitiveType.CHAR;

		assertTrue(type.isAssignableTo(type));
	}

	@Test
	public void testPrimitiveIsNotAssignableToSomethingElse()
	{
		Type type = PrimitiveType.CHAR;
		Type otherType = PrimitiveType.BOOLEAN;

		assertFalse(type.isAssignableTo(otherType));
	}
}
