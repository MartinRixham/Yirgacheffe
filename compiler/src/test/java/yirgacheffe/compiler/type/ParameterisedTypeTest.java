package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.TypeInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.lang.Array;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParameterisedTypeTest
{
	@Test
	public void testTypeWithOneParameter()
	{
		ReferenceType referenceType = new ReferenceType(List.class);
		Type typeParameter = new ReferenceType(String.class);
		Type type = new ParameterisedType(referenceType, new Array<>(typeParameter));

		assertEquals("java.util.List<java.lang.String>", type.toString());
		assertEquals(List.class, type.reflectionClass());
		assertEquals("java/util/List", type.toFullyQualifiedType());
		assertEquals("Ljava/util/List;", type.toJVMType());
		assertEquals("Ljava/util/List<Ljava/lang/String;>;", type.getSignature());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertTrue(type.hasParameter());
		assertFalse(type.isPrimitive());
	}

	@Test
	public void testTypeWithTwoParameters()
	{
		ReferenceType referenceType = new ReferenceType(Map.class);
		Type firstParameter = new ReferenceType(String.class);
		Type secondParameter = PrimitiveType.DOUBLE;
		Array<Type> parameters = new Array<>(firstParameter, secondParameter);
		Type type = new ParameterisedType(referenceType, parameters);

		assertEquals("java.util.Map<java.lang.String,Num>", type.toString());
	}

	@Test
	public void testStringIsNotAssignableToSystem()
	{
		ReferenceType reference =
			new ReferenceType(yirgacheffe.lang.MutableReference.class);

		Type string = new ReferenceType(String.class);
		Type system = new ReferenceType(System.class);

		Type stringReference = new ParameterisedType(reference, new Array<>(string));
		Type systemReference = new ParameterisedType(reference, new Array<>(system));

		assertFalse(stringReference.isAssignableTo(systemReference));
	}

	@Test
	public void testNotAssignableToReferenceType()
	{

		ReferenceType reference =
			new ReferenceType(yirgacheffe.lang.MutableReference.class);
		Type string = new ReferenceType(String.class);

		Type stringReference = new ParameterisedType(reference, new Array<>(string));

		assertFalse(stringReference.isAssignableTo(string));
	}

	@Test
	public void testNewArray()
	{
		ReferenceType referenceType = new ReferenceType(Random.class);
		Type type = new ParameterisedType(referenceType, new Array<>());

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		TypeInsnNode instruction = (TypeInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.ANEWARRAY, instruction.getOpcode());
		assertEquals(referenceType.toFullyQualifiedType(), instruction.desc);
	}
}
