package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ArrayTypeTest
{
	@Test
	public void testArray()
	{
		Type stringArray =
			new ArrayType("[Ljava.lang.String;", new ReferenceType(String.class));

		assertEquals("java.lang.String[]", stringArray.toString());
		assertEquals("[Ljava.lang.String;", stringArray.reflectionClass().getName());
		assertEquals("[Ljava/lang/String;", stringArray.toJVMType());
		assertEquals("[Ljava/lang/String;", stringArray.getSignature());
		assertEquals("java.lang.String[]", stringArray.toFullyQualifiedType());
		assertEquals(1, stringArray.width());
		assertEquals(Opcodes.ARETURN, stringArray.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, stringArray.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, stringArray.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, stringArray.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, stringArray.getZero());
		assertFalse(stringArray.hasParameter());
		assertFalse(stringArray.isPrimitive());
	}

	@Test
	public void testArrayIsAssignableToArray()
	{
		Type first =
			new ArrayType("[Ljava.lang.Object;", new ReferenceType(String.class));

		Type second =
			new ArrayType("[Ljava.lang.String;", new ReferenceType(Object.class));

		assertTrue(first.isAssignableTo(second));
	}

	@Test
	public void testArrayIsNotAssignableToAnythingElse()
	{
		Type first =
			new ArrayType("[Ljava.lang.Object;", new ReferenceType(String.class));

		Type second = PrimitiveType.BOOLEAN;

		assertFalse(first.isAssignableTo(second));
	}

	@Test
	public void testNewArray()
	{
		Type type =
			new ArrayType("[Ljava.lang.String;", new ReferenceType(String.class));

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testTypeConversion()
	{
		Type type =
			new ArrayType("[Ljava.lang.String;", new ReferenceType(String.class));

		Result result = type.convertTo(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testSwap()
	{
		Type type =
			new ArrayType("[Ljava.lang.String;", new ReferenceType(String.class));

		Result result = type.swapWith(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testIntersection()
	{
		Type type =
			new ArrayType("[Ljava.lang.String;", new ReferenceType(String.class));

		Type intersection = type.intersect(new ReferenceType(Object.class));

		assertEquals(new ReferenceType(Object.class), intersection);
	}

	@Test
	public void testComparison()
	{
		Type type =
			new ArrayType("[Ljava.lang.String;", new ReferenceType(String.class));

		Result result = type.compare(BooleanOperator.AND, new Label());

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}
}
