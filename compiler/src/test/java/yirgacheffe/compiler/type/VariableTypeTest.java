package yirgacheffe.compiler.type;

import org.objectweb.asm.Opcodes;
import org.junit.Test;
import yirgacheffe.compiler.Result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VariableTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier()
	{
		Class<?> loadedClass = Object.class;

		Type type = new VariableType("T");

		assertEquals("T", type.toString());
		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("java/lang/Object", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals("TT;", type.getSignature());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertFalse(type.isAssignableTo(new ReferenceType(Object.class)));
		assertTrue(type.hasParameter());
		assertFalse(type.isPrimitive());
	}

	@Test
	public void testNewArray()
	{
		Type type = new VariableType("T");

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}
}
