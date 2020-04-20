package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.junit.Test;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;

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
		assertTrue(type.reflect().doesImplement(loadedClass));
		assertEquals("java/lang/Object", type.toFullyQualifiedType());
		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals("TT;", type.getSignature());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertFalse(type.isAssignableTo(new BoundedType("S", type)));
		assertTrue(type.hasParameter());
		assertFalse(type.isPrimitive());
		assertTrue(type.getTypeParameter("") instanceof NullType);
	}

	@Test
	public void testNewArray()
	{
		Type type = new VariableType("T");

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testTypeConversion()
	{
		Type type = new VariableType("T");

		Result result = type.convertTo(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testSwap()
	{
		Type type = new VariableType("T");

		Result result = type.swapWith(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testIntersection()
	{
		Type type = new VariableType("T");

		Type intersection = type.intersect(new ReferenceType(Object.class));

		assertEquals(new ReferenceType(Object.class), intersection);
	}

	@Test
	public void testComparison()
	{
		Type type = new VariableType("T");

		Result result = type.compare(BooleanOperator.AND, new Label());

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}
}
