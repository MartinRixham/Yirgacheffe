package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.error.Coordinate;
import yirgacheffe.compiler.operator.BooleanOperator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NullTypeTest
{
	@Test
	public void testNullType()
	{
		Type type = new NullType();

		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals("Ljava/lang/Object;", type.getSignature());
		assertEquals("java/lang/Object", type.toFullyQualifiedType());
		assertEquals(0, type.construct(new Coordinate(0, 0)).getErrors().length());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertEquals("java.lang.Object", type.toString());
		assertFalse(type.hasParameter());
		assertFalse(type.isPrimitive());
		assertTrue(type.getTypeParameter("") instanceof NullType);
	}

	@Test
	public void testNullIsAssignableToString() throws Exception
	{
		Class<?> stringClass =
			Thread.currentThread()
				.getContextClassLoader()
				.loadClass("java.lang.String");

		Type string = new ReferenceType(stringClass);

		assertTrue(new NullType().isAssignableTo(string));
	}

	@Test
	public void testNullIsAssignableToBoolean()
	{
		assertTrue(new NullType().isAssignableTo(PrimitiveType.BOOLEAN));
	}

	@Test
	public void testNewArray()
	{
		Type type = new NullType();

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testTypeConversion()
	{
		Type type = new NullType();

		Result result = type.convertTo(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testSwap()
	{
		Type type = new NullType();

		Result result = type.swapWith(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testIntersection()
	{
		Type type = new NullType();

		Type intersection = type.intersect(new ReferenceType(Object.class));

		assertEquals(type, intersection);
	}

	@Test
	public void testComparison()
	{
		Type type = new NullType();

		Result result = type.compare(BooleanOperator.AND, new Label());

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}
}
