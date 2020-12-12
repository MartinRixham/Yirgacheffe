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

public class BoundedTypeTest
{
	@Test
	public void testBoundedType()
	{
		Class<?> loadedClass = Object.class;

		Type type = new BoundedType("T", new ReferenceType(Object.class));

		assertEquals("T", type.toString());
		assertTrue(type.reflect().doesImplement(loadedClass));
		assertTrue(type.reflect(type).doesImplement(loadedClass));
		assertEquals("java/lang/Object", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals("T:Ljava/lang/Object;", type.getSignature());
		assertEquals(0, type.construct(new Coordinate(0, 0)).getErrors().length());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertFalse(type.isAssignableTo(new ReferenceType(Object.class)));
		assertTrue(type.hasParameter());
		assertFalse(type.isPrimitive());
		assertTrue(type.getTypeParameter("") instanceof NullType);
	}

	@Test
	public void testNewArray()
	{
		Type type = new BoundedType("T", new ReferenceType(Object.class));

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testTypeConversion()
	{
		Type type = new BoundedType("T", new ReferenceType(Object.class));

		Result result = type.convertTo(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testSwap()
	{
		Type type = new BoundedType("T", new ReferenceType(Object.class));

		Result result = type.swapWith(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testIntersection()
	{
		Type type = new BoundedType("T", new ReferenceType(Object.class));

		Type intersection = type.intersect(new ReferenceType(Object.class));

		assertEquals(new ReferenceType(Object.class), intersection);
	}

	@Test
	public void testComparison()
	{
		Type type = new BoundedType("T", new ReferenceType(Object.class));

		Result result = type.compare(BooleanOperator.AND, new Label());

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}
}
