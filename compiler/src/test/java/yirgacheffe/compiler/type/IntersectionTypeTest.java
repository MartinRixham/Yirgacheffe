package yirgacheffe.compiler.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.junit.Test;
import org.objectweb.asm.tree.JumpInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class IntersectionTypeTest
{
	@Test
	public void testIntersectionType()
	{
		Type arrayList = new ReferenceType(java.util.ArrayList.class);
		Type linkedList = new ReferenceType(java.util.LinkedList.class);
		Type list = new ReferenceType(java.util.List.class);

		Type type = new IntersectionType(arrayList, linkedList);

		assertEquals("java.util.ArrayList", type.toString());
		assertEquals(arrayList.reflectionClass(), type.reflectionClass());
		assertEquals("java/util/ArrayList", type.toFullyQualifiedType());
		assertEquals("Ljava/util/ArrayList;", type.toJVMType());
		assertEquals("Ljava/util/ArrayList;", type.getSignature());
		assertEquals(1, type.width());
		assertEquals(Opcodes.ARETURN, type.getReturnInstruction());
		assertEquals(Opcodes.ASTORE, type.getStoreInstruction());
		assertEquals(Opcodes.AASTORE, type.getArrayStoreInstruction());
		assertEquals(Opcodes.ALOAD, type.getLoadInstruction());
		assertEquals(Opcodes.ACONST_NULL, type.getZero());
		assertFalse(type.hasParameter());
		assertFalse(type.isPrimitive());
		assertFalse(type.isAssignableTo(arrayList));
		assertFalse(type.isAssignableTo(linkedList));
		assertTrue(type.isAssignableTo(list));
		assertNotEquals(type, new ReferenceType(Object.class));
		assertTrue(type.getTypeParameter("") instanceof NullType);
	}

	@Test
	public void testNewArray()
	{
		Type arrayList = new ReferenceType(java.util.ArrayList.class);
		Type linkedList = new ReferenceType(java.util.LinkedList.class);

		Type type = new IntersectionType(arrayList, linkedList);

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());
	}

	@Test
	public void testTypeConversion()
	{
		Type arrayList = new ReferenceType(java.util.ArrayList.class);
		Type linkedList = new ReferenceType(java.util.LinkedList.class);

		Type type = new IntersectionType(arrayList, linkedList);

		Result result = type.convertTo(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testSwap()
	{
		Type arrayList = new ReferenceType(java.util.ArrayList.class);
		Type linkedList = new ReferenceType(java.util.LinkedList.class);

		Type type = new IntersectionType(arrayList, linkedList);

		Result result = type.swapWith(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testIntersection()
	{
		Type arrayList = new ReferenceType(java.util.ArrayList.class);
		Type linkedList = new ReferenceType(java.util.LinkedList.class);

		Type type = new IntersectionType(arrayList, linkedList);

		Type intersection = type.intersect(new ReferenceType(Object.class));

		assertTrue(intersection instanceof IntersectionType);
	}

	@Test
	public void testEquals()
	{
		Type random = new ReferenceType(Random.class);

		Type type = new IntersectionType(random, random);

		assertEquals(type, random);
		assertEquals(type.hashCode(), random.hashCode());
	}

	@Test
	public void testNotEqual()
	{
		Type arrayList = new ReferenceType(java.util.ArrayList.class);
		Type linkedList = new ReferenceType(java.util.LinkedList.class);

		Type type = new IntersectionType(arrayList, linkedList);

		assertNotEquals(type, arrayList);
		assertNotEquals(type, linkedList);
	}

	@Test
	public void testComparison()
	{
		Type arrayList = new ReferenceType(java.util.ArrayList.class);
		Type linkedList = new ReferenceType(java.util.LinkedList.class);

		Type type = new IntersectionType(arrayList, linkedList);
		Label label = new Label();

		Result result = type.compare(BooleanOperator.AND, label);

		assertEquals(0, result.getErrors().length());
		assertEquals(1, result.getInstructions().length());

		JumpInsnNode firstInstruction = (JumpInsnNode) result.getInstructions().get(0);

		assertEquals(Opcodes.IFNULL, firstInstruction.getOpcode());
		assertEquals(label, firstInstruction.label.getLabel());
	}
}
