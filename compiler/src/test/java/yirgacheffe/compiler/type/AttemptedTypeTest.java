package yirgacheffe.compiler.type;

import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import yirgacheffe.compiler.Result;
import yirgacheffe.compiler.operator.BooleanOperator;
import yirgacheffe.lang.Array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AttemptedTypeTest
{
	@Test
	public void testNullType()
	{
		Type type = new AttemptedType(PrimitiveType.DOUBLE);

		assertEquals("Ljava/lang/Object;", type.toJVMType());
		assertEquals(Object.class, type.reflectionClass());
		assertEquals(null, type.getSignature());
		assertEquals("java/lang/Object", type.toFullyQualifiedType());
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
	public void testNewArray()
	{
		Type type = new AttemptedType(PrimitiveType.DOUBLE);

		Result result = type.newArray();

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testTypeConversion()
	{
		Type type = new AttemptedType(PrimitiveType.DOUBLE);

		Result result = type.convertTo(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testTypeConversionToDouble()
	{
		Type type = new AttemptedType(PrimitiveType.INT);

		Result result = type.convertTo(PrimitiveType.DOUBLE);

		assertEquals(0, result.getErrors().length());
		assertEquals(2, result.getInstructions().length());

		Array<AbstractInsnNode> instructions = result.getInstructions();

		MethodInsnNode firstInstruction = (MethodInsnNode) instructions.get(0);

		assertEquals(Opcodes.INVOKESTATIC, firstInstruction.getOpcode());
		assertEquals("toInteger", firstInstruction.name);
		assertEquals("(Ljava/lang/Object;)I", firstInstruction.desc);

		InsnNode secondInstruction = (InsnNode) instructions.get(1);

		assertEquals(Opcodes.I2D, secondInstruction.getOpcode());
	}

	@Test
	public void testSwap()
	{
		Type type = new AttemptedType(PrimitiveType.DOUBLE);

		Result result = type.swapWith(new ReferenceType(Object.class));

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}

	@Test
	public void testIntersection()
	{
		Type type = new AttemptedType(PrimitiveType.DOUBLE);

		Type intersection = type.intersect(new ReferenceType(Object.class));

		assertEquals(new ReferenceType(Object.class), intersection);
	}

	@Test
	public void testComparison()
	{
		Type type = new AttemptedType(PrimitiveType.DOUBLE);

		Result result = type.compare(BooleanOperator.AND, new Label());

		assertEquals(0, result.getErrors().length());
		assertEquals(0, result.getInstructions().length());
	}
}
