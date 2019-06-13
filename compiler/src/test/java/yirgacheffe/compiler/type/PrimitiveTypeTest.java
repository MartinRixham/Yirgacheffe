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
		PrimitiveType type = PrimitiveType.VOID;

		assertEquals("Void", type.toString());
		assertEquals("java/lang/Void", type.toFullyQualifiedType());
		assertEquals("V", type.toJVMType());
		assertEquals("V", type.getSignature());
		assertEquals(0, type.width());
		assertEquals(Opcodes.RETURN, type.getReturnInstruction());
		assertEquals(Opcodes.NOP, type.getStoreInstruction());
		assertEquals(Opcodes.NOP, type.getArrayStoreInstruction());
		assertEquals(Opcodes.NOP, type.getLoadInstruction());
		assertEquals(Opcodes.NOP, type.getZero());
		assertEquals(Opcodes.NOP, type.getTypeInstruction());
		assertFalse(type.hasParameter());
		assertTrue(type.isPrimitive());
	}

	@Test
	public void testBoolIsPrimitive()
	{
		PrimitiveType type = PrimitiveType.BOOLEAN;

		assertEquals("Bool", type.toString());
		assertEquals("java/lang/Boolean", type.toFullyQualifiedType());
		assertEquals("Z", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.IRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ISTORE, type.getStoreInstruction());
		assertEquals(Opcodes.IASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ILOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ICONST_0, type.getZero());
	}

	@Test
	public void testCharIsPrimitive()
	{
		PrimitiveType type = PrimitiveType.CHAR;

		assertEquals("Char", type.toString());
		assertEquals("java/lang/Character", type.toFullyQualifiedType());
		assertEquals("C", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.IRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ISTORE, type.getStoreInstruction());
		assertEquals(Opcodes.IASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ILOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ICONST_0, type.getZero());
	}

	@Test
	public void testNumIsPrimitive()
	{
		PrimitiveType type = PrimitiveType.DOUBLE;

		assertEquals("Num", type.toString());
		assertEquals("java/lang/Double", type.toFullyQualifiedType());
		assertEquals("D", type.toJVMType());
		assertEquals(2, type.width());
		assertEquals(Opcodes.DRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.DSTORE, type.getStoreInstruction());
		assertEquals(Opcodes.DASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.DLOAD, type.getLoadInstruction());
		assertEquals(Opcodes.DCONST_0, type.getZero());
		assertEquals(Opcodes.T_DOUBLE, type.getTypeInstruction());
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
