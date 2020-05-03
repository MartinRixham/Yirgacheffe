package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ArrayTypeTest
{
	@Test
	public void testArrayType()
	{
		ReferenceType referenceType = new ReferenceType(Array.class);
		Array<Type> typeParameter = new Array<>(new ReferenceType(String.class));
		ParameterisedType type = new ParameterisedType(referenceType, typeParameter);
		Type arrayType = new ArrayType(type);

		assertEquals("yirgacheffe.lang.Array<java.lang.String>", arrayType.toString());
		assertTrue(arrayType.reflect().doesImplement(Array.class));
		assertTrue(arrayType.reflect(type).doesImplement(Array.class));
		assertEquals("yirgacheffe/lang/Array", arrayType.toFullyQualifiedType());
		assertEquals("Lyirgacheffe/lang/Array;", arrayType.toJVMType());

		assertEquals(
			"Lyirgacheffe/lang/Array<Ljava/lang/String;>;", arrayType.getSignature());

		assertEquals(1, arrayType.width());
		assertEquals(Opcodes.ARETURN, arrayType.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, arrayType.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, arrayType.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, arrayType.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, arrayType.getZero());
		assertTrue(arrayType.hasParameter());
		assertFalse(arrayType.isPrimitive());
		assertEquals(new ReferenceType(String.class), arrayType.getTypeParameter("T"));

		assertEquals(
			type.newArray().getInstructions().length(),
			arrayType.newArray().getInstructions().length());

		assertEquals(
			type.swapWith(type).getInstructions().length(),
			arrayType.swapWith(type).getInstructions().length());

		assertEquals(type.intersect(type), arrayType.intersect(type));

		Label label = new Label();

		assertEquals(
			type.compare(BooleanOperator.OR, label).getInstructions().length(),
			arrayType.compare(BooleanOperator.OR, label).getInstructions().length());

		assertEquals(referenceType.hashCode(), arrayType.hashCode());
		assertNotEquals(arrayType, "");
	}
}
