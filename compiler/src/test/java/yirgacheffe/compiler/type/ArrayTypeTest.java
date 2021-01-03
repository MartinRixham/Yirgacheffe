package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArrayTypeTest
{
	@Test
	public void testArray()
	{
		Type stringArray =
			new ArrayType("[Ljava.lang.String;", new ReferenceType(String.class));

		assertEquals("java.lang.String[]", stringArray.toString());
		assertEquals("String[]", stringArray.reflect().getSimpleName());
		assertEquals("String[]", stringArray.reflect(stringArray).getSimpleName());
		assertEquals("[Ljava/lang/String;", stringArray.toJVMType());
		assertEquals("[Ljava/lang/String;", stringArray.getSignature());
		assertArrayEquals(new String[0], stringArray.getSignatureTypes());
		assertEquals("java.lang.String[]", stringArray.toFullyQualifiedType());
		assertEquals(0, stringArray.construct(new Coordinate(0, 0)).getErrors().length());
		assertEquals(1, stringArray.width());
		assertEquals(Opcodes.ARETURN, stringArray.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, stringArray.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, stringArray.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, stringArray.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, stringArray.getZero());
		assertFalse(stringArray.hasParameter());
		assertFalse(stringArray.isPrimitive());
		assertTrue(stringArray.getTypeParameter("") instanceof NullType);
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
	public void testArrayIsAssignableToYirgacheffeArray()
	{
		Type type =
			new ArrayType("[Ljava.lang.Object;", new ReferenceType(String.class));

		ReferenceType arrayType = new ReferenceType(Array.class);
		Type objectType = new ReferenceType(Object.class);
		Type other = new ParameterisedType(arrayType, new Array<>(objectType));

		assertTrue(type.isAssignableTo(other));
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
	public void testConvertToArrayObject()
	{
		Type stringType = new ReferenceType(String.class);
		ReferenceType arrayType = new ReferenceType(Array.class);

		Type arrayObjectType = new ParameterisedType(arrayType, new Array<>(stringType));

		Type type =
			new ArrayType("[Ljava.lang.String;", new ReferenceType(String.class));

		Result result = type.convertTo(arrayObjectType);

		assertEquals(1, result.getInstructions().length());

		MethodInsnNode instruction = (MethodInsnNode) result.getInstructions().get(0);

		assertEquals("fromArray", instruction.name);
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

	@Test
	public void testEqualArrays() throws Exception
	{
		String descriptor = "[Ljava.lang.String;";

		Type firstType =
			new ArrayType(descriptor, new ReferenceType(String.class));

		Type secondType =
			new ArrayType(descriptor, new ReferenceType(String.class));

		Class<?> clazz = Class.forName(descriptor);

		assertEquals(firstType.hashCode(), clazz.hashCode());
		assertEquals(firstType, secondType);
	}
}
