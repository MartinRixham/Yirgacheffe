package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.assertEquals;

public class PrimitiveTypeTest
{
	@Test
	public void testVoidIsPrimitive()
	{
		Type type = PrimitiveType.VOID;

		assertEquals("void", type.toString());
		assertEquals("java.lang.Void", type.toFullyQualifiedType());
		assertEquals("V", type.toJVMType());
		assertEquals(0, type.width());
		assertEquals(Opcodes.RETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
	}

	@Test
	public void testBoolIsPrimitive()
	{
		Type type = PrimitiveType.BOOL;

		assertEquals("bool", type.toString());
		assertEquals("java.lang.Boolean", type.toFullyQualifiedType());
		assertEquals("Z", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.IRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ISTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ILOAD, type.getLoadInstruction());
	}

	@Test
	public void testCharIsPrimitive()
	{
		Type type = PrimitiveType.CHAR;

		assertEquals("char", type.toString());
		assertEquals("java.lang.Character", type.toFullyQualifiedType());
		assertEquals("C", type.toJVMType());
		assertEquals(1, type.width());
		assertEquals(Opcodes.IRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ISTORE, type.getStoreInstruction());
		assertEquals(Opcodes.ILOAD, type.getLoadInstruction());
	}

	@Test
	public void testNumIsPrimitive()
	{
		Type type = PrimitiveType.DOUBLE;

		assertEquals("num", type.toString());
		assertEquals("java.lang.Double", type.toFullyQualifiedType());
		assertEquals("D", type.toJVMType());
		assertEquals(2, type.width());
		assertEquals(Opcodes.DRETURN, type.getReturnInstruction());
		assertEquals(Opcodes.DSTORE, type.getStoreInstruction());
		assertEquals(Opcodes.DLOAD, type.getLoadInstruction());
	}
}
