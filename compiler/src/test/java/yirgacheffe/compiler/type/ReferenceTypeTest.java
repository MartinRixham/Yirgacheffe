package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ReferenceTypeTest
{
	@Test
	public void testTypeFromPackageAndIdentifier()
	{
		Class<?> loadedClass = java.lang.String.class;

		Type type = new ReferenceType(loadedClass);

		assertEquals("java.lang.String", type.toString());
		assertEquals(loadedClass, type.reflectionClass());
		assertEquals("java/lang/String", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/String;", type.toJVMType());
		assertEquals("Ljava/lang/String;", type.getSignature());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertFalse(type.hasParameter());
		assertFalse(type.isPrimitive());
	}

	@Test
	public void testStringIsAssignableToObject()
	{
		Class<?> stringClass = java.lang.String.class;
		Class<?> objectClass = java.lang.Object.class;

		Type string = new ReferenceType(stringClass);
		Type object = new ReferenceType(objectClass);

		assertTrue(string.isAssignableTo(object));
	}

	@Test
	public void testStringIsNotAssignableToSystem()
	{
		Class<?> stringClass = java.lang.String.class;
		Class<?> systemClass = java.lang.System.class;

		Type string = new ReferenceType(stringClass);
		Type system = new ReferenceType(systemClass);

		assertFalse(string.isAssignableTo(system));
	}

	@Test
	public void testEqualTypes()
	{
		Type first = new ReferenceType(String.class);
		Type second = new ReferenceType(String.class);

		assertEquals(first, second);
		assertEquals(first.hashCode(), first.reflectionClass().hashCode());
	}

	@Test
	public void testNewArray()
	{
		Type type = new ReferenceType(Random.class);

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		TypeInsnNode instruction = (TypeInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.ANEWARRAY, instruction.getOpcode());
		assertEquals(type.toFullyQualifiedType(), instruction.desc);
	}

	@Test
	public void testTypeConversion()
	{
		Type type = new ReferenceType(Random.class);

		Result result = type.convertTo(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}
}
